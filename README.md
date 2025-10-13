# blog-back-app

Backend application for blog system which provides API to work with posts.

# Project build

Project uses Gradle VCS.

1. Clone repository
```
git clone https://github.com/Olegka35/blog-back-app
cd <project-directory>
```
2. Build project into .war file

```
./gradlew clean build
```
3. Deploy created .war file into Tomcat project directory:

``` 
<TOMCAT_HOME>/webapps/
```

# Execute tests
```
./gradlew test
```
