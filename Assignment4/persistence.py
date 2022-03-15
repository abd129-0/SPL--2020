import atexit
import sqlite3


# Data Transfer Objects:
class Vaccine:
    def __init__(self, id, date, supplier, quantity):
        self.id = id
        self.date = date
        self.supplier = supplier
        self.quantity = quantity


class Supplier:
    def __init__(self, id, name, logistic):
        self.id = id
        self.name = name
        self.logistic = logistic


class Clinic:
    def __init__(self, id, location, demand, logistic):
        self.id = id
        self.location = location
        self.demand = demand
        self.logistic = logistic


class Logistic:
    def __init__(self, id, name, count_sent, count_received):
        self.id = id
        self.name = name
        self.count_sent = count_sent
        self.count_received = count_received


# Data Access Objects:
# All of these are meant to be singletons
class Vaccines:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, vaccine):
        self._conn.execute("""
               INSERT INTO vaccines VALUES(?,?,?,?)
           """, [vaccine.id, vaccine.date, vaccine.supplier, vaccine.quantity])

    def next_key(self):
        cursor = self._conn.cursor()
        cursor.execute("""SELECT max(id) FROM vaccines""")
        return cursor.fetchone()[0] + 1

    def all_vaccines(self):
        cursor = self._conn.cursor()
        cursor.execute("""SELECT * FROM vaccines ORDER BY date ASC""")
        return cursor.fetchall()

    def update(self, vaccine_id, new_quantity):
        self._conn.execute("""UPDATE vaccines
        SET quantity=(?)
        WHERE id=(?)""", [new_quantity, vaccine_id])

    def delete(self, vaccine_id):
        self._conn.execute("""DELETE FROM vaccines WHERE id=(?)""", [vaccine_id])

    def inventory(self):
        cursor = self._conn.cursor()
        cursor.execute("SELECT quantity FROM vaccines")
        quantities = cursor.fetchall()
        inventory = 0
        for num in quantities:
            inventory = inventory + num[0]
        return inventory


class Suppliers:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, supplier):
        self._conn.execute("""
               INSERT INTO suppliers VALUES(?,?,?)
           """, [supplier.id, supplier.name, supplier.logistic])

    def find_id(self, supplier_name):
        c = self._conn.cursor()
        c.execute("""
            SELECT id FROM suppliers WHERE name=(?)
        """, [supplier_name])

        return c.fetchone()[0]

    def find_logistic(self, supplier_name):
        c = self._conn.cursor()
        c.execute("""
            SELECT logistic FROM suppliers WHERE name=(?)
        """, [supplier_name])

        return c.fetchone()[0]


class Clinics:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, clinic):
        self._conn.execute("""
               INSERT INTO clinics VALUES(?,?,?,?)
           """, [clinic.id, clinic.location, clinic.demand, clinic.logistic])

    def find(self, location):
        cursor = self._conn.cursor()
        cursor.execute("""
               SELECT * FROM clinics WHERE location=(?)
           """, [location])
        return cursor.fetchone()

    def update_demand(self, location, new_demand):
        self._conn.execute("""UPDATE clinics
            SET demand=(?)
            WHERE location=(?)""", [new_demand, location])

    def demand(self):
        cursor = self._conn.cursor()
        cursor.execute("SELECT demand FROM clinics")
        demands = cursor.fetchall()
        demand = 0
        for num in demands:
            demand = demand + num[0]
        return demand


class Logistics:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, logistic):
        self._conn.execute("""
               INSERT INTO logistics VALUES(?,?,?,?)
           """, [logistic.id, logistic.name, logistic.count_sent, logistic.count_received])

    def increase_count_received(self, logistics_id, amount):
        cursor = self._conn.cursor()
        cursor.execute("""
               SELECT count_received FROM logistics WHERE id=(?)
           """, (logistics_id,))
        quantity = cursor.fetchone()[0]
        self._conn.execute("""UPDATE logistics
        SET count_received=(?)
        WHERE id=(?)""", [quantity + amount, logistics_id])

    def increase_count_sent(self, logistics_id, amount):
        cursor = self._conn.cursor()
        cursor.execute("""
               SELECT count_sent FROM logistics WHERE id=(?)
           """, (logistics_id,))
        quantity = cursor.fetchone()[0]
        self._conn.execute("""UPDATE logistics
        SET count_sent=(?)
        WHERE id=(?)""", [quantity + amount, logistics_id])


# The Repository
class _Repository:
    def __init__(self):
        self._conn = sqlite3.connect('database.db')
        self.Vaccines = Vaccines(self._conn)
        self.Suppliers = Suppliers(self._conn)
        self.Clinics = Clinics(self._conn)
        self.Logistics = Logistics(self._conn)

    def _close(self):
        self._conn.commit()
        self._conn.close()

    def create_tables(self):
        self._conn.executescript("""
        CREATE TABLE vaccines (
            id      INTEGER      PRIMARY KEY,
            date    DATE         NOT NULL,
            supplier INTEGER    REFERENCES Supplier(id),
            quantity INTEGER    NOT NULL
        );

        CREATE TABLE suppliers (
            id      INTEGER      PRIMARY KEY,
            name    STRING       NOT NULL,
            logistic INTEGER    REFERENCES Logistic(id)
        );
        
        CREATE TABLE clinics (
            id          INTEGER     PRIMARY KEY,
            location    STRING      NOT NULL,
            demand      INTEGER     NOT NULL,
            logistic    INTEGER     REFERENCES Logistic(id)
        );
        
        CREATE TABLE logistics (
        id              INTEGER     PRIMARY KEY,
        name            STRING      NOT NULL,
        count_sent      INTEGER     NOT NULL,
        count_received  INTEGER     NOT NULL
        );
    """)

# the repository singleton
repo = _Repository()
repo.create_tables()
atexit.register(repo._close)
