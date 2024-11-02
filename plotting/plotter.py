import matplotlib.pyplot as plt
import socket
import Capacity_pb2

# Define server connections
servers = [("localhost", 8081), ("localhost", 8082), ("localhost", 8083)]  # Example ports

def fetch_capacity_data():
    data = []
    for ip, port in servers:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.connect((ip, port))
            s.sendall(Capacity_pb2.Message(demand=Capacity_pb2.Message.CPCTY).SerializeToString())
            data.append(Capacity_pb2.Capacity().ParseFromString(s.recv(1024)))
    return data

def plot_data(data):
    for server_data in data:
        plt.plot(server_data.timestamp, server_data.server_status)
    plt.legend(["Server1", "Server2", "Server3"])
    plt.show()

while True:
    data = fetch_capacity_data()
    plot_data(data)
    time.sleep(5)
