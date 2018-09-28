# config server

externalize config

## file system backend

`spring.profiles.active=native`로 실행시켜야 합니다. 이것은 빠르게 테스트할 때 용의합니다. 별도의 경로를 지정하고 싶을 경우에는 `spring.cloud.config.server.native.searchLocations`를 지정하면 됩니다.
지정하지 않았을 경우에는 아래의 경로가 기본입니다.

* `classpath:/`
* `classpath:/config`
* `file:./`
* `file:./config`

## Encryption and Decryption Secure Data

Spring Cloud Config Server의 Encryption / Decryption 기능을 사용하기 위해서는 **Full-Strength(또는 Unlimited) JCE(Java Cryptography Extension)가 JVM에 설치**가 되어 있어야 합니다. (이것은 기본적으로 설치가 되어 있지 않습니다.)

### Project Setup and Dependencies

Spring Cloud Config Server Project에 `spring-boot-starter-security` module을 추가해 줍니다.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- 위의 module이 포함하고 있으므로 명시적으로 추가해 주지 않아도 됩니다. -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

기본적으로 `spring-boot-starter-security`는 HTTP Basic Authentication을 지원하고 기본 `username`으로 `user`, `password`는 기동시에 Random하게 생성해 줍니다. 이것을 막기 위해 아래의 내용을 application.yml(또는 application.properties)에 추가해 줍니다.

```yaml
spring:
  security:
    user:
      name: "root"
      password: "s3cr3t"
```

### Key Managements

Config Server는 Symmetric(Shared) Key 방식과 Asymmetric Key (RSA Key Pair) 방식을 사용할 수 있습니다. Asymmetric Key 방식이 좀 더 보안에 강점이 있지만, 하나의 속성 값만 설정해서 사용할 수 있는 Symmetric Key 방식도 종종 사용되곤 합니다.

> 이 값들은 `bootstrap.yml`(or `bootstrap.properties`) File에 설정합니다.

#### Symmetric Key Setup

```yaml
# filename: src/main/resources/bootstrap.yml
encrypt:
  key: "s3cr3t"
```

#### Asymmetric Key Setup

Java Keytool을 사용하여 Key Store를 생성합니다.

```bash
$ keytool -genkeypair -alias config-server-key \
      -keyalg RSA -keysize 4096 -sigalg SHA512withRSA \
      -dname "CN=Config Server, OU=BrainAge Network, O=BrainAge" \
      -keypass my-k34-s3cr3t -keystore config-server.jks \
      -storepass my-s70r3-s3cr3t
```

위에서 생성된 `config-server.jks` 파일을 `${project.base.dir}\src\main\resources`에 위치 시키고 `bootstrap.yml` 파일에 아래와 같이 내용을 추가합니다.

```yaml
# filename: src/main/resources/bootstrap.yml
encrypt:
  key-store:
    location: "classpath:/config-server.jks"
    password: "my-s70r3-s3cr3t"
    alias: "config-server-key"
    secret: "my-k34-s3cr3t"
```

### Spring Security Setup

Spring Security의 Auto Configuration에 의해 CSRF(Cross-Site Request Forgery) 기능이 활성화 되어 있는 상태입니다. 이런 경우, 정상적으로 요청의 수행이 되지 않으므로 비활성화시켜야 합니다. 

아래와 같이 Java Config로 CSRF를 비활성화 합니다.

```java
package net.brainage.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
    }

}

```

### Encrypt Secure Data

Config Server는 데이타 암호화를 위하여 `/encrypt` endpoint를 제공합니다.

```bash
$ curl -X POST root:s3cr3t@localhost:8888/encrypt --data-urlencode admin
$ curl -X POST -H "Authorization: Basic cm9vdDpzM2NyM3Q=" localhost:8888/encrypt --data admin
```
위와 같이 해서 나온 값을 아래와 같이 설정에 적용합니다.

```yaml
spring:
  datasource:
    password: "{cipher}FKslkfjaoiufqwoiu204dsu2304jflf"
```

## References

* [Spring Cloud Config Reference Guide](https://cloud.spring.io/spring-cloud-config/multi/multi_spring-cloud-config.html)
* [Quick Intro to spring Cloud Configuration](https://www.baeldung.com/spring-cloud-configuration)
