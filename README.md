# Application

This application is working ....   
It analyze stocks on GPW (Warsaw Stock Exchange) and creates signals witch you can see:
- here https://x.com/piotr_slaszu
- and here https://gpwapisignals.blogspot.com/p/o-blogu.html


# native image agent

```
./gradlew bootJar
java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image/ -jar build/libs/stockanalyzer.jar
```


