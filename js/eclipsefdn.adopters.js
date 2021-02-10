/*!
 * Copyright (c) 2019 Eclipse Foundation, Inc.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * Contributors:
 *   Eric Poirier <eric.poirier@eclipse-foundation.org>
 * 
 * SPDX-License-Identifier: EPL-2.0
*/

(function(root, factory) {
  if (typeof define === 'function' && define.amd) {
    define(['efa'], factory(root));
  }
  else if (typeof exports === 'object') {
    module.exports = factory(require('efa'));
  }
  else {
    root.eclipseFdnAdopters = factory(root, root.efa);
  }
})(this, function(efa) {

  'use strict';

  // Define object
  var eclipseFdnAdopters = {};
  var precompiledRegex = /<([^>]*?)>;(\s?[\w-]*?="(?:\\"|[^"])*";){0,}\s?rel="next"/;
  // Default settings
  var default_options = {
    project_id: '',
    selector: '.eclipsefdn-adopters',
    ul_classes: '',
    logo_white: false,
    working_group: '',
    src_root: 'https://api.eclipse.org/adopters',
    src_projects_prefix: '/projects'
  };

  function getMergedOptions(options) {
    // Default settings copy
    var opts = JSON.parse(JSON.stringify(default_options));

    // Go through the parameters of Options if its defined and is an object
    if (typeof (options) !== 'undefined' && typeof (options) === 'object') {
      for (var optionName in default_options) {
        if (typeof (options[optionName]) === 'undefined' || (typeof (options[optionName]) !== 'string' && typeof (options[optionName]) !== 'boolean')) {
          continue;
        }
        opts[optionName] = options[optionName];
      }
    }
    return opts;
  }

  /**
   * Replace the adopters container
   * @public
   * @param {Object} options Videos attributes
   */
  eclipseFdnAdopters.getList = function(options) {
    var opts = getMergedOptions(options);
    console.log(opts)
    fireCall(opts, function(response) {
      createProjectList(response, opts, document.querySelectorAll(opts.selector));
      scrollToAnchor();
    });
  }

  /**
   * Replace the adopters container
   * @public
   * @param {Object} options Videos attributes
   */
  eclipseFdnAdopters.getWGList = function(options) {
    var opts = getMergedOptions(options);
    // create callback on ready
    fireCall(opts, function(response) {
      createWGProjectsList(response, opts, document.querySelectorAll(opts.selector));
      scrollToAnchor();
    });
  }

  function fireCall(opts, callback, currentData = []) {
    var xhttp = new XMLHttpRequest();
    // create callback on ready
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        // merge new data with current
        var json = JSON.parse(this.responseText);
        if (Array.isArray(currentData) || currentData.length) {
          json = currentData.concat(json);
        }

        // check the link header as long as its set
        var linkHeader = xhttp.getResponseHeader('Link');
        if (linkHeader !== null) {
          var match = linkHeader.match(precompiledRegex);
          // if there is no match, then there is no next and we are on the last page and should process data through callback
          if (match !== null) {
            opts.next = match[1];
            fireCall(opts, callback, json);
          } else {
            callback(json);
          }
        } else {
          callback(json);
        }

      } else if (this.readyState == 4) {
        console.log('Error while retrieving adopters data, could not complete operation');
      }
    };

    // get the URL to call, using the 'next' url if set, otherwise building from original option set
    var url;
    if (opts.next !== undefined) {
      url = opts.next;
    } else {
      url = opts.src_root + opts.src_projects_prefix;
      if (opts.project_id !== undefined && opts.project_id.trim() !== '') {
        url += '/' + opts.project_id;
      }
      if (opts.working_group !== undefined && opts.working_group.trim() !== '') {
        url += '?working_group=' + opts.working_group;
      }
    }
    // send request to get JSON data
    xhttp.open('GET', url, true);
    xhttp.send();
  }

  function createWGProjectsList(json_object, opts, el) {
    for (const project of json_object) {
      var projectOpts = JSON.parse(JSON.stringify(opts));
      projectOpts.project_id = project.project_id;

      // add the title
      const h2 = document.createElement('h2');
      h2.textContent = project.name;
      h2.setAttribute('id', project.project_id);
      for (var i = 0; i < el.length; i++) {
        el[i].append(h2);
      }
      const headerAnchor = document.createElement('a');
      headerAnchor.setAttribute('class', 'btn btn-xs btn-secondary margin-left-10 uppercase');
      headerAnchor.setAttribute('href', 'https://projects.eclipse.org/projects/' + project.project_id);
      headerAnchor.textContent = project.project_id;
      h2.appendChild(headerAnchor);

      createProjectList(json_object, projectOpts, el);
    }
  }

  function createProjectList(json_object, opts, el) {
    const ul = document.createElement('ul');
    if (typeof json_object !== 'undefined') {
      for (const project of json_object) {
        if (opts.project_id !== project.project_id) {
          continue;
        }
        for (const adopter of project.adopters) {
          // Get the home page url of this adopter
          var url = '';
          if (typeof adopter['homepage_url'] !== 'undefined') {
            url = adopter['homepage_url'];
          }

          // Get the name of this adopter
          var name = '';
          if (typeof adopter['name'] !== 'undefined') {
            name = adopter['name'];
          }

          // Get the logo of this adopter
          var logo = '';
          if (typeof adopter['logo'] !== 'undefined') {
            logo = adopter['logo'];
          }
          if (opts['logo_white'] === true && typeof adopter['logo_white'] !== 'undefined') {
            logo = adopter['logo_white'];
          }

          // Create the html elements
          let li = document.createElement('li');
          let a = document.createElement('a');
          let img = document.createElement('img');

          a.setAttribute('href', url);
          img.setAttribute('alt', name);
          img.setAttribute('src', opts.src_root + '/assets/images/adopters/' + logo);
          img.setAttribute('class', 'adopters-img');

          a.appendChild(img);
          li.appendChild(a);
          ul.appendChild(li);
        }
      }
    }
    for (var i = 0; i < el.length; i++) {
      if (opts['ul_classes'] !== '') {
        ul.setAttribute('class', opts['ul_classes']);
      }
      el[i].append(ul);
    }
  }

  // Function to scroll when there is anchor in url
  function scrollToAnchor() {
    if (location.hash) {
      var projectId = location.hash.replace('#', '');
      var element = document.getElementById(`${projectId}`);
      element.scrollIntoView();
    }
  }

  return eclipseFdnAdopters;
});