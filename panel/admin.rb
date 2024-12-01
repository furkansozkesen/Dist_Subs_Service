require 'socket'
require 'json'

class AdminPanel
  CONFIG_FILE = "dist_subs.conf"

  def initialize
    @servers = []
    load_configuration
  end

  def load_configuration
    if File.exist?(CONFIG_FILE)
      config_data = File.read(CONFIG_FILE)
      config = JSON.parse(config_data)
      @fault_tolerance_level = config["fault_tolerance_level"]
      @servers = config["servers"]
    else
      puts "Configuration file not found. Please provide #{CONFIG_FILE}."
    end
  end

  def send_command_to_servers(command)
    @servers.each do |server_info|
      host, port = server_info["host"], server_info["port"]
      begin
        socket = TCPSocket.new(host, port)
        socket.puts(command)
        response = socket.gets
        puts "Response from #{host}:#{port} => #{response.strip}"
        socket.close
      rescue => e
        puts "Failed to connect to #{host}:#{port} - #{e.message}"
      end
    end
  end

  def start_servers
    send_command_to_servers("START")
  end

  def stop_servers
    send_command_to_servers("STOP")
  end
end

if __FILE__ == $0
  admin = AdminPanel.new
  puts "1. Start Servers"
  puts "2. Stop Servers"
  choice = gets.chomp.to_i

  case choice
  when 1
    admin.start_servers
  when 2
    admin.stop_servers
  else
    puts "Invalid choice."
  end
end
