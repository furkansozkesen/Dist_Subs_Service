# Dağıtık Abonelik Sistemi (Distributed Subscriber Service)

## Proje Hakkında
Bu proje, Java, Ruby ve Python dilleriyle dağıtık bir sistem uygulamasını içerir. Sistem, sunucular arasında iletişim kurarak veri akışını sağlar ve belirli görevleri yerine getirir. Projede kullanılan Google Protocol Buffers (Protobuf) sayesinde farklı diller arasında veri alışverişi kolaylaştırılmıştır.

## Kurulum ve Çalıştırma Adımları

### 1. dist_servers Klasöründe Protobuf Dosyalarını Derleme
Protobuf dosyalarını derlemek için aşağıdaki komutları kullanın:
```sh
protoc --java_out=. Capacity.proto
protoc --java_out=. Subscriber.proto
```

### 2. dist_servers Klasöründe Sunucuları Derleme
Server.java dosyalarını derlemek için aşağıdaki komutu çalıştırın:
```sh
javac -cp ".;com/google/protobuf/protobuf-java-4.28.3.jar" *.java
```

### 3. dist_servers Klasöründe Sunucuları Çalıştırma
Sunucuları sırayla çalıştırmak için aşağıdaki komutları kullanın:
```sh
java -cp ".;com/google/protobuf/protobuf-java-4.28.3.jar" Server1
java -cp ".;com/google/protobuf/protobuf-java-4.28.3.jar" Server2
java -cp ".;com/google/protobuf/protobuf-java-4.28.3.jar" Server3
```

### 4. panel Klasöründe Ruby Dosyalarını Derleme
Ruby için Protobuf dosyalarını derlemek için şu komutu çalıştırın:
```sh
protoc --ruby_out=. Capacity.proto
```

### 5. panel Klasöründe Ruby Scriptini Çalıştırma
admin.rb dosyasını çalıştırmak için şu komutu kullanın:
```sh
ruby admin.rb
```

### 6. plotting Klasöründe Protobuf Dosyalarını Derleme
Python için Protobuf dosyalarını derlemek için aşağıdaki komutu kullanın:
```sh
protoc --python_out=. Capacity.proto
```

### 7. plotting Klasöründe Python Scriptini Çalıştırma
plotter.py dosyasını çalıştırmak için şu komutu kullanın:
```sh
python plotter.py
```

### 8. Clients Klasöründe Protobuf Dosyalarını Derleme
Clients klasöründeki Protobuf dosyalarını derlemek için şu komutu çalıştırın:
```sh
protoc --java_out=. Subscriber.proto
```

### 9. Clients Klasöründe Java Dosyalarını Derleme
Client.java dosyalarını derlemek için şu komutu çalıştırın:
```sh
javac -cp ".;com/google/protobuf/protobuf-java-4.28.3.jar" *.java
```

### 10. Clients Klasöründe Java Dosyalarını Sırasıyla çalıştırma
Client.java dosyalarını çalıştırmak için şu komutları çalıştırın:
```sh
java -cp ".;com/google/protobuf/protobuf-java-4.28.3.jar" Client
java -cp ".;com/google/protobuf/protobuf-java-4.28.3.jar" Client1
java -cp ".;com/google/protobuf/protobuf-java-4.28.3.jar" Client2
```

## Tamamlanan Gorevler:
- **Ruby Scripti (admin.rb):**
  - Ruby scripti admin.rb, Server.java dosyalarıyla bağlantı kurabiliyor.
  - Ayrıca, admin.rb ile Python scripti plotter.py arasında da bağlantı sağlanmıştır.

- **Server.java İletişimi:**
  - Server.java dosyaları kendi aralarında başarıyla abone bilgilerini paylaşabiliyor ve yedekleme işlemlerini gerçekleştirebiliyor. Bu, sunucuların birbirleriyle etkili bir şekilde iletişim kurabildiğini göstermektedir.

## Proje Hakkında Kapanış Notları
Bu proje, dağıtık sistemler, protokol tasarımı ve çok dilli programlama yaklaşımları hakkında derin bir anlayış sağladı. Her adımda detaylı testler yapıldı ve sistemin belirli kısımları başarıyla çalıştırıldı.

Projenin geliştirilmesi sırasında büyük bir emek harcanmıştır.




### plotter.py Solar System Exploration, 1950s – 1960s

- [ ] Mercury
- [x] Venus
- [x] Earth (Orbit/Moon)
- [x] Mars
- [ ] Jupiter
- [ ] Saturn
- [ ] Uranus
- [ ] Neptune
- [ ] Comet Haley

### admin.rb Solar System Exploration, 1950s – 1960s

- [ ] Mercury
- [x] Venus
- [x] Earth (Orbit/Moon)
- [x] Mars
- [ ] Jupiter
- [ ] Saturn
- [ ] Uranus
- [ ] Neptune
- [ ] Comet Haley

### ServerX.java Solar System Exploration, 1950s – 1960s

- [ ] Mercury
- [x] Venus
- [x] Earth (Orbit/Moon)
- [x] Mars
- [ ] Jupiter
- [ ] Saturn
- [ ] Uranus
- [ ] Neptune
- [ ] Comet Haley


### SUNUM VİDEOSU LİNKİ:
[YouTube Video](https://youtu.be/iIuaf6u1aiM)
https://youtu.be/iIuaf6u1aiM
