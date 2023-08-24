# PowlyPollington

Simple polling tool developed in Scala and, initially, intended to be run within a 
docker/podman container

**Suggested docker/podman usage**

Create a new application.conf and update the 
associated filename referenced in DockerInstallation file. Build your container 
locally using something like:
```
docker run -d \
   -p 9000:9000 \
   -v /path/to/your/local/powly/db:/opt/powly \
   -v /path/to/your/local/powly/db/conf/application.conf:/usr/src/app/conf/application.conf \
   --name powly \
   ghcr.io/diinnk/powlypollington:latest
```

**TODO:**
 - Vote validation logic
 - Delete a poll
 - Edit a poll
   - edit title
   - edit desc
   - edit poll options
   - add a poll option
   - delete a poll option
   - edit poll voting attribution
 - Edit FE
 - Fix some funky results pages when all the options have short names
 - Authentication for creation, deletion, modification and result viewing of polls
 - Swagger docs
 - Prettier front-end
 - Various warnings and error handling on config definition for default usernames/passwords and the like
 - Request throttling to stop spam, injection etc