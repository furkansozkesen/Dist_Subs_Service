require 'socket'
require 'google/protobuf'
require_relative 'Configuration_pb'
require_relative 'Message_pb'
require_relative 'Capacity_pb'

CONFIG_PATH = "dist_subs.conf"
PORT_LIST = [5001, 5002, 5003]

def parse_config
  tolerance_level = nil
  File.foreach(CONFIG_PATH) do |line|
    key, val = line.strip.split('=', 2)
    tolerance_level = val.to_i if key == 'fault_tolerance_level'
  end
  tolerance_level
end

def interact_with_server(connection, tolerance_level)
  start_msg = Configuration.new(fault_tolerance_level: tolerance_level, method_type: Configuration::MethodType::STRT)
  connection.write(start_msg.serialize_to_string)
  puts "Başlatma mesajı sunucuya gönderildi."

  response_data = connection.read
  server_response = Message.decode(response_data)
  puts "Sunucudan yanıt alındı: İstek -> #{server_response.demand}, Yanıt -> #{server_response.response}"

  capacity_query = Message.new(demand: Message::Demand::CPCTY, response: Message::Response::NULL)
  connection.write(capacity_query.serialize_to_string)
  puts "Kapasite sorgulama mesajı sunucuya gönderildi."

  capacity_data = connection.read
  capacity_info = Capacity.decode(capacity_data)
  puts "Sunucu kapasite durumu alındı: Durum -> #{capacity_info.serverX_status}, Zaman -> #{capacity_info.timestamp}"
end

tolerance_level = parse_config

PORT_LIST.each do |port|
  begin
    connection = TCPSocket.open("localhost", port)
    puts "Sunucu bağlantısı sağlandı: Port -> #{port}"
    interact_with_server(connection, tolerance_level)
    connection.close
  rescue Errno::ECONNREFUSED
    puts "Bağlantı başarısız: Port -> #{port}"
  end
end
