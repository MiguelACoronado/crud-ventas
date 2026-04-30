CREATE TABLE IF NOT EXISTS customers (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(150) UNIQUE
)@@

CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    price NUMERIC(12, 2) NOT NULL CHECK (price >= 0),
    stock INTEGER NOT NULL CHECK (stock >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)@@

CREATE TABLE IF NOT EXISTS sales (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    subtotal NUMERIC(12, 2) NOT NULL CHECK (subtotal >= 0),
    tax NUMERIC(12, 2) NOT NULL CHECK (tax >= 0),
    discount NUMERIC(12, 2) NOT NULL CHECK (discount >= 0),
    total NUMERIC(12, 2) NOT NULL CHECK (total >= 0),
    sale_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)@@

CREATE TABLE IF NOT EXISTS employees (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    position VARCHAR(80) NOT NULL,
    salary NUMERIC(12, 2) NOT NULL CHECK (salary >= 0),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)@@

CREATE TABLE IF NOT EXISTS employee_audit (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT,
    action_type VARCHAR(20) NOT NULL,
    old_data JSONB,
    new_data JSONB,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)@@

INSERT INTO customers (full_name, email)
SELECT 'Cliente Demo', 'cliente.demo@correo.com'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE email = 'cliente.demo@correo.com')@@

CREATE OR REPLACE FUNCTION fn_calcular_iva(p_subtotal NUMERIC, p_porcentaje NUMERIC DEFAULT 19)
RETURNS NUMERIC
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN ROUND(p_subtotal * (p_porcentaje / 100.0), 2);
END;
$$@@

CREATE OR REPLACE FUNCTION fn_aplicar_descuento(p_subtotal NUMERIC, p_descuento_porcentaje NUMERIC DEFAULT 0)
RETURNS NUMERIC
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN ROUND(p_subtotal * (p_descuento_porcentaje / 100.0), 2);
END;
$$@@

CREATE OR REPLACE FUNCTION fn_total_factura(
    p_subtotal NUMERIC,
    p_iva_porcentaje NUMERIC DEFAULT 19,
    p_descuento_porcentaje NUMERIC DEFAULT 0
)
RETURNS NUMERIC
LANGUAGE plpgsql
AS $$
DECLARE
    v_iva NUMERIC;
    v_descuento NUMERIC;
BEGIN
    v_iva := fn_calcular_iva(p_subtotal, p_iva_porcentaje);
    v_descuento := fn_aplicar_descuento(p_subtotal, p_descuento_porcentaje);
    RETURN ROUND(p_subtotal + v_iva - v_descuento, 2);
END;
$$@@

CREATE OR REPLACE FUNCTION fn_total_con_impuestos(
    p_subtotal NUMERIC,
    p_iva_porcentaje NUMERIC DEFAULT 19
)
RETURNS NUMERIC
LANGUAGE plpgsql
AS $$
DECLARE
    v_iva NUMERIC;
BEGIN
    v_iva := fn_calcular_iva(p_subtotal, p_iva_porcentaje);
    RETURN ROUND(p_subtotal + v_iva, 2);
END;
$$@@

CREATE OR REPLACE PROCEDURE sp_registrar_venta(
    IN p_customer_id BIGINT,
    IN p_subtotal NUMERIC,
    IN p_iva_porcentaje NUMERIC,
    IN p_descuento_porcentaje NUMERIC,
    INOUT p_sale_id BIGINT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_iva NUMERIC;
    v_descuento NUMERIC;
    v_total NUMERIC;
BEGIN
    v_iva := fn_calcular_iva(p_subtotal, p_iva_porcentaje);
    v_descuento := fn_aplicar_descuento(p_subtotal, p_descuento_porcentaje);
    v_total := fn_total_factura(p_subtotal, p_iva_porcentaje, p_descuento_porcentaje);

    INSERT INTO sales(customer_id, subtotal, tax, discount, total)
    VALUES (p_customer_id, p_subtotal, v_iva, v_descuento, v_total)
    RETURNING id INTO p_sale_id;
END;
$$@@

CREATE OR REPLACE PROCEDURE sp_total_vendido_por_cliente(
    IN p_customer_id BIGINT,
    INOUT p_total NUMERIC
)
LANGUAGE plpgsql
AS $$
BEGIN
    SELECT COALESCE(SUM(s.total), 0)
    INTO p_total
    FROM sales s
    WHERE s.customer_id = p_customer_id;
END;
$$@@

CREATE OR REPLACE FUNCTION fn_auditar_empleados()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO employee_audit(employee_id, action_type, old_data, new_data)
        VALUES (NEW.id, TG_OP, NULL, to_jsonb(NEW));
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO employee_audit(employee_id, action_type, old_data, new_data)
        VALUES (NEW.id, TG_OP, to_jsonb(OLD), to_jsonb(NEW));
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO employee_audit(employee_id, action_type, old_data, new_data)
        VALUES (OLD.id, TG_OP, to_jsonb(OLD), NULL);
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$@@
DO $$
DECLARE
    v_trigger_name TEXT;
BEGIN
    IF to_regclass('public.customers') IS NOT NULL THEN
        DROP TRIGGER IF EXISTS trg_audit_customers ON customers;
    END IF;
    IF to_regclass('public.products') IS NOT NULL THEN
        DROP TRIGGER IF EXISTS trg_audit_products ON products;
    END IF;
    IF to_regclass('public.sales') IS NOT NULL THEN
        DROP TRIGGER IF EXISTS trg_audit_sales ON sales;
    END IF;
    IF to_regclass('public.reservations') IS NOT NULL THEN
        DROP TRIGGER IF EXISTS trg_audit_reservations ON reservations;
    END IF;
    IF to_regclass('public.employees') IS NOT NULL THEN
        FOR v_trigger_name IN
            SELECT tgname
            FROM pg_trigger
            WHERE tgrelid = 'public.employees'::regclass
              AND NOT tgisinternal
        LOOP
            EXECUTE format('DROP TRIGGER IF EXISTS %I ON employees', v_trigger_name);
        END LOOP;
        CREATE TRIGGER trg_audit_employees
        AFTER INSERT OR UPDATE OR DELETE ON employees
        FOR EACH ROW EXECUTE FUNCTION fn_auditar_empleados();
    END IF;
END;
$$@@
