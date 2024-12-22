import socket
from Capacity_pb2 import Capacity
import matplotlib.pyplot as plt

HOST = 'localhost'
PORT = 6000

MAX_LENGTH = 10  # Grafikte gösterilecek maksimum sütun sayısı
subscriber_counts = []  # Gelen abone sayılarının listesini tutacağız
MAX_SUBSCRIBER = 100  # Gelen abone sayısını normalize etmek için maksimum değer

def plot_subscriber_count():
    plt.clf()  # Grafiği temizle
    normalized_counts = [count / MAX_SUBSCRIBER for count in subscriber_counts]  # Verileri normalize et
    x_positions = range(len(normalized_counts))  # X ekseni için sütun konumları
    plt.bar(x_positions, normalized_counts, color='blue', edgecolor='black')  # Sütun grafiği
    plt.ylabel('Subscriber Count (Normalized)', fontsize=14)
    plt.title('Recent Subscriber Counts (0-1 Scale)', fontsize=16)
    plt.ylim(0, 1.0)  # Y ekseni sınırını 0 ile 1 arasında ayarla
    plt.xticks(x_positions, [f"{i+1}" for i in x_positions], fontsize=12)
    plt.yticks(fontsize=12)
    plt.tight_layout()
    plt.draw()
    plt.pause(0.01)

def start_server():
    plt.ion()  # Etkileşimli modu aç
    fig = plt.figure()  # Grafik için bir figure oluştur
    plt.show()  # Grafik penceresini başlat

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server_socket:
        server_socket.bind((HOST, PORT))
        server_socket.listen(1)
        print(f"Python server listening on {HOST}:{PORT}")

        while True:  # Sürekli dinleme
            conn, addr = server_socket.accept()
            with conn:
                print(f"Connected by {addr}")
                data = b""  # Gelen veriyi depolamak için
                while True:
                    chunk = conn.recv(1024)
                    data += chunk
                    if not chunk:  # Veri bitene kadar okumaya devam et
                        break
                if data:
                    capacity = Capacity()
                    capacity.ParseFromString(data)  # Gelen veriyi parse et
                    print(f"Received subscriber count: {capacity.subscriber_count}")
                    subscriber_counts.append(capacity.subscriber_count)  # Abone sayısını listeye ekle

                    # Listenin uzunluğunu sınırla
                    if len(subscriber_counts) > MAX_LENGTH:
                        subscriber_counts.pop(0)  # En eski veriyi kaldır

                    plot_subscriber_count()  # Grafiği güncelle

if __name__ == "__main__":
    start_server()
