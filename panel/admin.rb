require 'socket'
require 'protobuf'
require_relative "C:/Users/frkns/OneDrive/Masaüstü/Dist_Subs_Service/panel/capacity_pb"

PYTHON_SERVER_PORT = 6000 # Python server port
ADMIN_HOST = 'localhost'  # Admin sunucu adresi

class AdminPanel
  SERVER_PORTS = [4001, 4002, 4003]

  def initialize
    @server_sockets = {}
    @plotter_socket = nil
  end

  # Sunuculara bağlan
  def start_server
    SERVER_PORTS.each do |port|
      Thread.new do
        begin
          server = TCPServer.new(port)
          puts "Server listening on port #{port}"

          loop do
            begin
              client = server.accept
              handle_client(client, port)
            rescue => e
              puts "Error handling client on port #{port}: #{e.message}"
            ensure
              client&.close
            end
          end
        rescue => e
          puts "Failed to start server on port #{port}: #{e.message}"
        end
      end
    end
  end

  # İstemciyi işle
  def handle_client(socket, port)
    puts "Client connected on port #{port}"

    begin
      loop do
        data = socket.readpartial(1024) # Veriyi parçalar halinde oku
        capacity = Capacity.decode(data) # Protobuf mesajını çöz
        puts "Received capacity on port #{port}: #{capacity.subscriber_count}"

        send_to_plotter(capacity.subscriber_count, port)
      end
    rescue EOFError
      puts "Client disconnected on port #{port}"
    rescue => e
      puts "Error processing client on port #{port}: #{e.message}"
    end
  end

  # Kapasite bilgilerini Plotter'a gönder
  def send_to_plotter(subscriber_count, port)
    begin
      subscriber_count = 0 if subscriber_count.nil? || subscriber_count.to_s.strip.empty?
      capacity = Capacity.new(
        server_port: port,
        subscriber_count: subscriber_count
      )
      serialized_data = capacity.to_proto
      @plotter_socket ||= TCPSocket.new(ADMIN_HOST, PYTHON_SERVER_PORT)
      @plotter_socket.write(serialized_data) # Veriyi yeni satırla gönder
      @plotter_socket.flush
      puts "Sent subscriber count to plotter: #{subscriber_count}"
    rescue => e
      puts "Error sending to plotter: #{e.message}"
    ensure
      @plotter_socket&.close
      @plotter_socket = nil
    end
  end
end

# AdminPanel başlat
admin_panel = AdminPanel.new


loop do
  admin_panel.start_server
  sleep(5) # 5 saniyede bir kontrol et ve gönder
end
