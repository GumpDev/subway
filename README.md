<div align="center">
    <img width="200px" src="subway.png" />
    <h1>Subway</h1>
    <p>Better way to communicate local microservices</p>
</div>

<br>

## Topics
- [🚆 What is Subway?](#whatissubway)
- [🔧 Installation](#installation)
- [📝 Getting Started](#getting-started)
- [🤔 FAQ](#faq)
- [🙏 Thanks](#thanks)

<br>
<a id="whatissubway"></a>

## 🚆 What is Subway?

Subway it's a Library to make communications into your microservices (Recommend locally)


<br>
<a id="installation"></a>

## 🔧 Installation

### Installation with maven
First, add GumpDev's repository into ```pom.xml```:
```xml
<repositories>
    ...
    <repository>
        <id>gump-dev</id>
        <url>https://repo.gump.dev/snapshots/</url>
    </repository>
</repositories>
```

Then, add Subway's dependency too:
```xml
<dependencies>
    ...
    <dependency>
        <groupId>dev.gump</groupId>
        <artifactId>Subway</artifactId>
        <version>1.0-SNAPSHOT</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

<br>

### Installation with Gradle
First, add Gumpdev's repository into ```build.gradle```:
```
repositories {
    ...
    mavenCentral()
    maven {
        name = 'gump-dev'
        url = 'https://repo.gump.dev/snapshots/'
    }
}
```

Then, add Subway's dependency too:
```
    dependencies {
        ...
        compile 'dev.gump:Subway:1.0-SNAPSHOT'
    }
```
(I don't know if this is right, I don't use Gradle :P)

<br>
<a id="getting-started"></a>

## 📝 Getting Started

To get started we need to start Subway in your Server, use in main class:
```java
public static void main(String[] args) {
    Subway.init(new SubwayConfig("localhost", 33990));
}
```

Now you can create a Class that represents transactions, extends Train in Server service:
```java
public class TestTrain extends Train<String>{
    int id;
    
    //This will be the data in class, that will be delivered to the server
    public TestTrain(int id){
        this.id = id;
    }
    
    //This will be the function called on the server
    @Override
    public String process(){
        return "processed id " + id;
    }
}
```

Then we register the Class on Server service:
```java
public static void main(String[] args) {
    Subway.registerClass(TestTrain.class);
    Subway.init(new SubwayConfig("localhost", 33990));
}
```

In the Client service you open the connection with a Tunnel:
```java
    Tunnel tunnel = new Tunnel(new TunnelConfig("localhost", 33990));
```

When you need to communicate with the server you can send, importing classes from your server services through pom.xml:
```java
    TestTrain testTrain = new TestTrain(10);
    testTrain.send(tunnel).get() //processed id 10
```


If you want to learn more [Click here to see documentation](https://github.com/GumpDev/subway/wiki)

<br>
<a id="faq"></a>

## 🤔 FAQ

- **Why you created that?** *I needed to communicate with a microservice but I won't want to use http locally, so i created subway*
- **I Found a BUG!** *[Click here](https://github.com/GumpDev/subway/issues) and open an issue*
- **Can I help with the project?** *Sure! just send your PR :D*
- **Can I contact you?** *Yep, send email to contact@gump.dev*

<br>
<a id="thanks"></a>

## 🙏 Thanks
Thanks to [SimpleNet](https://github.com/jhg023/SimpleNet)
