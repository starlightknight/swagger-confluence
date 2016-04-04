# Swagger Confluence

## Overview

The primary goal of this project is to simplify publishing Swagger API documentation to an Atlassian Confluence server. Ideally, this plugin would be activated on the build of a schema jar, which would contain your contract-first Swagger JSON or YAML file



While this was possible via stringing together other projects, it was not easy to drop in and use.



This project uses the following flow to publish to Confluence:



Your Swagger Schema -> Swagger2Markup -> AsciiDoctorJ -> XHTML -> Confluence REST API



This project requires the use of Java 7 or later.

## Components

The project is current broken into three components

- **swagger-confluence-core:** a core shared library to handle converting and publishing the swagger schema to confluence. This shared library should be reusable by other projects
- **swagger-confluence-gradle-plugin:** a gradle plugin to provide seamless conversion and publishing
- **swagger-confluence-cli:** a command line executor for publishing to swagger docs to confluence

## Documentation & Usage Guide

You can view the documentation and usage guide [here](https://cloud.slkdev.net/swagger-confluence)

## Live Demo

You can view a live demo of Swagger Confluence [here on my Confluence Server](https://cloud.slkdev.net/confluence/display/DEMO/Swagger+Confluence+Demos)

## Contributing

### Community contributions

Pull requests are welcome. The primary source repository for this project is hosted on my [GitLab CE Instance](https://cloud.slkdev.net/gitlab/starlightknight/swagger-confluence).


It is preferred that if you wish to contribute, you submit there as this is where I have CI, SonarQube, etc set up. GitLab CE officially provides OmniAuth integration to make this easy, or you can of course sign up manually as well.


GitLab CE is open source software. If you are unfamiliar with it, you can read more about it [here](https://about.gitlab.com/) on their official web site.


If you are not comfortable with this, you may submit via the GitHub mirror, and I will merge it back manually.

### Questions, Bugs, or Enhacement Requests



If you have any questions about the project, please feel free to open an [issue](https://cloud.slkdev.net/gitlab/starlightknight/swagger-confluence/issues)



If you believe you have found a bug in the project, please take a moment to search the existing issues before posting a new one. If there is no existing issue regarding the problem, please open a new issue that describes the problem in detail. If possible, please also include a unit test that reproduces the problem.



If you would like an enhancement to be made to the Swagger Confluence, pull requests are welcome. Before beginning work on an enhancement, you may want to search the existing issues and pull requests to see if a similar enhancement is already being worked on. You may also want to open a new issue to discuss the enhancement.



## Static Code Analysis + Coverage

See [SonarQube](https://cloud.slkdev.net/sonar/overview?id=30) for this project

## Special Thanks


A special thanks for the following projects, who make this project possible:


* [Swagger2Markup](https://github.com/Swagger2Markup/swagger2markup)
* [AsciiDoctorJ](https://github.com/asciidoctor/asciidoctorj)
* [Confluence REST API](https://developer.atlassian.com/confdev/confluence-rest-api)



Additional shout-outs to the following two projects - whom I tried using manually in
conjunction with the above projects and their respective gradle plugins before starting this project:

* [asciidoctor-confluence](https://github.com/gscheibel/asciidoctor-confluence)
* [asciidoc2confluence](https://github.com/rdmueller/asciidoc2confluence)

The XHTML->Confluence REST API portion of this library was originally inspired by the
asciidoc2confluence groovy script

## License

Copyright 2016 Aaron Knight



Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at



    http://www.apache.org/licenses/LICENSE-2.0



Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
