import sqlite3
import sys
import config
from persistence import repo
import persistence

total_inventory = 0
total_demand = 0
total_received = 0
total_sent = 0
output_list = []


def init_inv():
    global total_inventory
    global total_demand
    total_inventory = repo.Vaccines.inventory()
    total_demand = repo.Clinics.demand()


def add_output():
    output_list.append(
        str(total_inventory) + "," + str(total_demand) + "," + str(total_received) + "," + str(total_sent) + "\n")


def report():
    file = open(str(sys.argv[3]), "w")
    for line in output_list:
        file.write(line)
    file.close()


def receive_shipment(args):
    logistics_id = repo.Suppliers.find_logistic(args[0])
    supplier_id = repo.Suppliers.find_id(args[0])
    new_id = repo.Vaccines.next_key()
    repo.Vaccines.insert(persistence.Vaccine(new_id, args[2], supplier_id, args[1]))
    repo.Logistics.increase_count_received(logistics_id, int(args[1]))
    global total_received
    global total_inventory
    total_received = total_received + int(args[1])
    total_inventory = total_inventory + int(args[1])
    add_output()


def send_shipment(args):
    clinic = repo.Clinics.find(args[0])
    demand = int(clinic[2])
    repo.Clinics.update_demand(args[0], demand - int(args[1]))
    vaccines = repo.Vaccines.all_vaccines()
    required_quantity = int(args[1])
    i = 0
    while required_quantity > 0 and i < len(vaccines):  # select one, fetch one
        vaccine = vaccines[i]
        i = i + 1
        if required_quantity > vaccine[3]:
            required_quantity = required_quantity - vaccine[3]
            repo.Logistics.increase_count_sent(clinic[3], vaccine[3])
            repo.Vaccines.delete(int(vaccine[0]))
        else:
            repo.Logistics.increase_count_sent(clinic[3], required_quantity)
            repo.Vaccines.update(vaccine[0], vaccine[3] - required_quantity)
            required_quantity = 0
    global total_inventory
    global total_demand
    global total_sent
    total_inventory = total_inventory - int(args[1])
    total_demand = total_demand - int(args[1])
    total_sent = total_sent + int(args[1])
    add_output()


def orders():
    inputfilename = sys.argv[2]
    with open(inputfilename) as inputfile:
        for line in inputfile:
            args = line.replace('\n', '').split(',')
            if len(args) == 2:
                send_shipment(args)
            else:
                receive_shipment(args)
    report()


if __name__ == '__main__':
    config.parse_config(sys.argv)
    init_inv()
    orders()
