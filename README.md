# Systems Privacy Project 2

Made by **Pedro Galego** Nº72298 and **Diogo Nunes** Nº70502

http://171.25.193.9:443/tor/status-vote/current/consensus

mvn install:install-file \
  -Dfile=generated/dist/metrics-lib-2.9.1.jar \
  -DgroupId=org.torproject \
  -DartifactId=metrics-lib \
  -Dversion=2.9.1 \
  -Dpackaging=jar
