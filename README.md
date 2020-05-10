## Run

```
mvn clean package

.\run.bat
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
