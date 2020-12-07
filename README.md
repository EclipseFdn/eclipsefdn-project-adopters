# eclipsefdn-project-adopters

[![Build Status](https://travis-ci.org/EclipseFdn/eclipsefdn-project-adopters.svg?branch=master)](https://travis-ci.org/EclipseFdn/eclipsefdn-project-adopters)

## Getting started

Install dependencies, build website and start a simple webserver:

```bash
npm install && hugo server
```

## Contributing

1. [Fork](https://help.github.com/articles/fork-a-repo/) the [EclipseFdn/eclipsefdn-project-adopters](https://github.com/EclipseFdn/eclipsefdn-project-adopters) repository
2. Clone repository: `git clone https://github.com/[your_github_username]/eclipsefdn-project-adopters.git`
3. Create your feature branch: `git checkout -b my-new-feature`
4. Commit your changes: `git commit -m 'Add some feature' -s`
5. Push feature branch: `git push origin my-new-feature`
6. Submit a pull request

## Project Adopters

Does your organization use Eclipse projects? Organizations — whether they are members of the Eclipse Foundation or not — can be listed as Eclipse technology adopters.

Adopters are organizations that voluntarily show their support for the Eclipse projects they have adopted (i.e. shipping commercial products based on the projects and/or using the projects for non-commercial or internal reasons). On this website, adopters are displayed under the [/adopters](https://eclipsefdn-project-adopters/adopters/) virtual path.

You can add your organization logo to our list of adopters by submitting a pull request or by creating an [issue](https://github.com/EclipseFdn/eclipsefdn-project-adopters/issues/new?template=adopter_request.md). You can attach files to an issue by dragging and dropping them in the text editor of the form.

If you plan on submitting a pull request, you will need to make the following changes to the website's codebase: 

1. Add a colored and a white organization logo to static/assets/images/adoptors. We expect that all submitted logos to be transparent svg and compressed for web.  
2. Update the adopter JSON file: [config/adopters.json](https://github.com/EclipseFdn/eclipsefdn-project-adopters/blob/master/config/adopters.json). Organizations can be easily marked as having multiple adopted projects across different working groups, no need to create separate entries for different projects or working groups!  

### Javascript Plugin 

Eclipse projects can showcase the logos of their adopters on their project websites. We built a JavaScript plugin to make this process easier. If you are a project committer, here are quick instructions on how to use the eclipsefdn-adopters.js on your Eclipse projet website:

#### Usage

Include the plugin's JS in the <head> section of the page:

```html
<script src="//eclipsefdn-project-adopters/assets/js/eclipsefdn.adopters.js"></script>
```

Load the plugin:

```
<script>
  eclipseFdnAdopters.getList({
    project_id: "[project_id]"
  });
</script>
```

Create an HTML element containing the chosen selector:

```
<div class="eclipsefdn-adopters"></div>
```
* By default, the selector's value is ```eclipsefdn-adopters```.

#### Options

```
<script>
  eclipseFdnAdopters.getList({
    project_id: "[project_id]",
    selector: ".eclipsefdn-adopters",
    ul_classes: "list-inline",
    logo_white: false
  });
</script>
```

Attribute     | Type        | Default   | Description
---           | ---         | ---       | ---
`project_id`   | *String*   | ` `    | **Required**: Select adopters from a specific project ID.
`working_group`   | *String*   | ` `    | **Required**: Select adopters from a specific working group ID.
`selector`   | *String*   | `.eclipsefdn-adopters`    | Define the selector that the plugin will insert adopters into.
`ul_classes`  | *String*   | ` `   | Define classes that will be assigned to the ul element.
`logo_white`  | *Boolean*   | `false`   | Whether or not we use the white version of the logo.

##### list of working groups for param `working_group`
- jakarta-ee
- automotive
- cloud-development-tools
- internet-things-iot
- locationtech
- openmdm
- polarsys
- research
- science
- sparkplug
- tangle-ee

### Declared Project Licenses

This program and the accompanying materials are made available under the terms
of the Eclipse Public License v. 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

SPDX-License-Identifier: EPL-2.0

## Bugs and Feature Requests

Have a bug or a feature request? Please search for existing and closed issues. If your problem or idea is not addressed yet, [please open a new issue](https://github.com/EclipseFdn/eclipsefdn-project-adopters/issues/new).

## Maintainers

**Christopher Guindon (Eclipse Foundation)**

- <https://twitter.com/chrisguindon>
- <https://github.com/chrisguindon>

**Eric Poirier (Eclipse Foundation)**

- <https://twitter.com/ericpoir>
- <https://github.com/ericpoirier>

## Trademarks
* Eclipse® is a Trademark of the Eclipse Foundation, Inc.

## Copyright and license

Copyright 2018 the [Eclipse Foundation, Inc.](https://www.eclipse.org) and the [eclipsefdn-project-adopters o authors](https://github.com/EclipseFdn/eclipsefdn-project-adopters/graphs/contributors). Code released under the [Eclipse Public License Version 2.0 (EPL-2.0)](https://github.com/EclipseFdn/eclipsefdn-project-adopters/blob/src/LICENSE).
