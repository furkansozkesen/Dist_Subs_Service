require 'socket'
require 'protobuf'
require_relative "C:/Users/frkns/OneDrive/Masaüstü/Dist_Subs_Service/panel/capacity_pb"

PYTHON_SERVER_PORT = 6000 # Python server port
ADMIN_HOST = 'localhost'  # Admin sunucu adresi

class AdminPanel
  SERVER_PORTS = [6001, 6002, 6003]

  def initialize
    @server_sockets = {}
    @plotter_socket = nil
  end

  # Sunuculara bağlan
  def connect_to_servers
    SERVER_PORTS.each do |port|
      connected = false
      until connected
        begin
          socket = TCPSocket.new('localhost', port)
          @server_sockets[port] = socket
          connected = true
          puts "Port #{port} ile bağlantı kuruldu"
        rescue => e
          puts "Port #{port} ile bağlantı başarısız: #{e.message}. Yeniden denenecek..."
          sleep(2)
        end
      end
    end
  end

  # Kapasite bilgilerini Plotter'a gönder
  def send_capacity_to_plotter
    begin
      # Plotter'a bağlan
      @plotter_socket = TCPSocket.new(ADMIN_HOST, PYTHON_SERVER_PORT)
      puts "Plotter soketi ile bağlantı kuruldu"
    rescue => e
      puts "Plotter soketi ile bağlantı başarısız: #{e.message}"
      return
    end

    @server_sockets.each do |port, socket|
      begin
        # Gelen veriyi kontrol et
        puts("Port #{port}: Bağlantıdan veri okunuyor...")
        response = socket.readpartial(1024)  # Veri okumayı başlat
        if response.nil? || response.empty?
          puts "Port #{port}: Veri boş, atlanıyor..."
          next
        end

        # Veri formatını kontrol et (örneğin, Protobuf)
        begin
          capacity_response = Capacity.decode(response)  # Protobuf'dan çözümle
        rescue => e
          puts "Port #{port}: Veriyi çözümleme hatası: #{e.message}"
          next
        end

        # Abone sayısını al
        subscriber_count = capacity_response.subscriber_count
        puts "Port #{port}: Abone sayısı: #{subscriber_count}"

        # Abone sayısını Plotter'a gönder
        @plotter_socket.write(subscriber_count.to_s) # Abone sayısını gönder
        puts "Port #{port}: Abone sayısı Plotter'a gönderildi"
      rescue EOFError
        puts "Port #{port}: Bağlantı kapandı, yeniden bağlanmaya çalışılıyor..."
        reconnect_to_server(port)
      rescue => e
        puts "Port #{port} için Plotter'a veri gönderim hatası: #{e.message}"
      end
    end
  ensure
    @plotter_socket.close if @plotter_socket
  end

  # Sunucu bağlantısını yeniden kur
  def reconnect_to_server(port)
    @server_sockets.delete(port)
    begin
      socket = TCPSocket.new('localhost', port)
      @server_sockets[port] = socket
      puts "Port #{port} ile yeniden bağlantı kuruldu"
    rescue => e
      puts "Port #{port} ile yeniden bağlanılamadı: #{e.message}"
    end
  end
end


# AdminPanel başlat
admin_panel = AdminPanel.new

loop do
  admin_panel.connect_to_servers
  admin_panel.send_capacity_to_plotter
  sleep(1) # Gereksiz CPU yükünü önlemek için
end
