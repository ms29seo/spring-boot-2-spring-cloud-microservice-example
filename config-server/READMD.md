# config server

externalize config

## file system backend

`spring.profiles.active=native`로 실행시켜야 합니다. 이것은 빠르게 테스트할 때 용의합니다. 별도의 경로를 지정하고 싶을 경우에는 `spring.cloud.config.server.native.searchLocations`를 지정하면 됩니다.
지정하지 않았을 경우에는 아래의 경로가 기본입니다.

* `classpath:/`
* `classpath:/config`
* `file:./`
* `file:./config`

## References

* [Spring Cloud Config Reference Guide](https://cloud.spring.io/spring-cloud-config/multi/multi_spring-cloud-config.html)
* [Quick Intro to spring Cloud Configuration](https://www.baeldung.com/spring-cloud-configuration)
