# obelisk-exporter

A [prometheus](prometheus.io) exporter for the obelisk ui.

## Docker

A docker image is provided at `akeboshiwind/obelisk-exporter`.

## Usage

    $ docker --rm akeboshiwind/obelisk-exporter

## Environment Variables

Variable | Description | Default
-------- | ----------- | -------
OBELISK_SERVER_ADDRESS | The address of the obelisk panel | None (required)
BASIC_AUTH_USER | The username to log in with basic auth | None (not required)
BASIC_AUTH_PASSWORD | The password to log in with basic auth | None (not required)
OBELISK_PANEL_USER | The username to log into the panel | admin
BASIC_AUTH_PASSWORD | The password to log into the panel | admin

## License

Copyright © 2019 Oliver Marshall

Distributed under the MIT License
