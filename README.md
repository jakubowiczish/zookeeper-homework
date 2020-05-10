## Run

### Important:

 
```Zookeper``` directory should be in ```C:\``` - main directory 
of disk on which this project is located: ```C:\Zookeeper``` 

Then, in separate terminals:

```bash
mvn clean package

.\run.bat
```

```bash
.\run_serv1.bat

.\run_serv2.bat

.\run_serv3.bat

.\run_cli1.bat

.\run_cli2.bat

.\run_cli3.bat
```

#### Zadanie domowe

Stworzyć aplikację w środowisku Zookeeper (Java, …) która
wykorzystując mechanizm obserwatorów (watches) umożliwia
następujące funkcjonalności:

– Jeśli tworzony jest znode o nazwie „z” uruchamiana jest zewnętrzna
aplikacja (dowolna, określona w linii poleceń),

– Jeśli jest kasowany „z” aplikacja zewnętrzna jest zatrzymywana,

– Każde dodanie potomka do „z” powoduje wyświetlenie graficznej
informacji na ekranie o aktualnej ilości potomków.

• Dodatkowo aplikacja powinna mieć możliwość wyświetlenia całej
struktury drzewa „z”.

• Stworzona aplikacja powinna działać w środowisku „Replicated
ZooKeeper”.

• ZooKeeper 3.5.7 API -
http://zookeeper.apache.org/doc/r3.5.7/apidocs/zookeeperserver/index.html
