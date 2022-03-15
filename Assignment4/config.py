from persistence import repo
import persistence


def parse_config(args):
    inputfilename = args[1]
    with open(inputfilename) as inputfile:
        first_line = inputfile.readline().replace('\n', '').split(',')
        number_int = int(first_line[0])
        while number_int > 0:
            line = inputfile.readline().replace('\n', '').split(',')
            repo.Vaccines.insert(persistence.Vaccine(line[0], line[1], line[2], line[3]))
            number_int = number_int - 1
        number_int = int(first_line[1])
        while number_int > 0:
            line = inputfile.readline().replace('\n', '').split(',')
            repo.Suppliers.insert(persistence.Supplier(line[0], line[1], line[2]))
            number_int = number_int - 1
        number_int = int(first_line[2])
        while number_int > 0:
            line = inputfile.readline().replace('\n', '').split(',')
            repo.Clinics.insert(persistence.Clinic(line[0], line[1], line[2], line[3]))
            number_int = number_int - 1
        number_int = int(first_line[3])
        while number_int > 0:
            line = inputfile.readline().replace('\n', '').split(',')
            repo.Logistics.insert(persistence.Logistic(line[0], line[1], line[2], line[3]))
            number_int = number_int - 1
