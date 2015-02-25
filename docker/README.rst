BUILD
=====

    $ docker build -t glassfish:4 .


SETUP MYSQL
===========


    $ docker pull mysql:5
    $ docker run -d --name mysql -e MYSQL_ROOT_PASSWORD=root mysql:5 
    $ alias docker-ip="docker inspect --format '{{ .NetworkSettings.IPAddress }}'"
    $ wget https://raw.githubusercontent.com/tyrion/meteocal/master/calcareInstall.sql
    $ mysql -h `docker-ip meteocal` -uroot -proot < calcareInstall.sql


RUN
===

    $ docker run -i -t --name calcare -p 8080:8080 -p 4848:4848 -v /opt/maven:/opt/maven --link mysql:db -e REPO=https://github.com/tyrion/meteocal.git -d glassfish:4 start

    $ docker attach --no-stdin=true calcare
    $ docker stop calcare
    $ docker start calcare
