This project is based on an outdated version of Conclave, 0.4.
Soon it will be migrated to 1.0.

Before that, to run this project one is required to point (in configuration files) to a valid 0.4 distribution.

To run this example:

1. Run a Docker container.
    ```shell script
    docker run -it --rm -p 9999:9999 -v $PWD:/project -w /project --user $(id -u):$(id -g) conclave-build /bin/bash
    ```
2. Run assembly and installation of the host
    ```shell script
    ./gradlew host:assembly
    ./gralew host:installDist
    ```

3. Run the host in the container:
    ```shell script
    ./host/build/install/host/bin/host
    ```

4. Run the client
    ```shell script
    ./gradlew client:run
    ```
