# obelisk-exporter

A [prometheus](prometheus.io) exporter for the obelisk ui.

## Docker

A docker image is provided at `akeboshiwind/obelisk-exporter`.

## Usage

    $ docker --rm akeboshiwind/obelisk-exporter

## Config File

Obelisk-exporter accepts a config file, the path to which can be specified using the `--config.file` parameter.

The format for the config file and it's defaults is as follows (keys without values have no default):
```yaml
general:
  port: 3000
  server_address: '0.0.0.0'

obelisk-ui:
  server_address:
  panel:
    user: admin
    password: admin
  basic-auth:
    user:
    password:
```

Only the `obelisk-ui.service_address` key is the only key that is required.

## Command Line Usage

```bash
usage: java -jar obelisk-exporter.jar [<flags>]

An exporter for the obelisk miner's panel

Flags:
  -h, --help              Shows this message
      --config.file PATH  Config YAML file
```

## Environment Variables


## Environment Variables

Variable | Description | Default
-------- | ----------- | -------
OBELISK_SERVER_ADDRESS | The address of the obelisk panel | None (required)
BASIC_AUTH_USER | The username to log in with basic auth | None (not required)
BASIC_AUTH_PASSWORD | The password to log in with basic auth | None (not required)
OBELISK_PANEL_USER | The username to log into the panel | admin
OBELISK_PANEL_PASSWORD | The password to log into the panel | admin

## License

Copyright © 2019 Oliver Marshall

Distributed under the MIT License
