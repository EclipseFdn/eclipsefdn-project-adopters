const eclipsefdn_adopters_element = document.querySelectorAll(".eclipsefdn-adopters")[0];

function testAdopters(baseURL) {
	fetch(`${baseURL}/assets/js/adopters.json`)
	.then(response => {
		if (response.ok) {
			return response.json();
		} else {
			return Promise.reject(response);
		}
	})
	.then(data => createWGAdopters(data.adopters, baseURL))
	.catch(error => console.log(error))
}

function createWGAdopters(data, baseURL) {

    var project_set = new Set();
    for(var i=0; i<data.length; i++) {

        // Loop the projects of this adopter
        for(var j=0; j<data[i].projects.length; j++) {
			var project = data[i].projects[j];

            // If not exists this project
            if( !project_set.has(project) ) {
                project_set.add(project);
                // add the title
				createProjectHeader(project);
			}
			createAdopters(project, data[i], baseURL);
		}
	}
}

function createProjectHeader(project) {
	var h2 = document.createElement('h2');
	h2.textContent = project;
	h2.setAttribute("id", project);
	eclipsefdn_adopters_element.append(h2);
	// add the button
	var headerAnchor = document.createElement('a');
	headerAnchor.setAttribute('class', 'btn btn-xs btn-secondary margin-left-10');
	headerAnchor.setAttribute('href', 'https://projects.eclipse.org/projects/' + project);
	headerAnchor.textContent = project;
	h2.appendChild(headerAnchor);
	var ul = document.createElement('ul');
	ul.setAttribute("class", "text-center list-inline");
	ul.setAttribute("id", `${project}-ul`);
	eclipsefdn_adopters_element.append(ul);
}

function createAdopters(project, adopter, baseURL) {
	var element = document.getElementById(`${project}-ul`);

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
	// Create the html elements
	var li = document.createElement('li');
	var a = document.createElement('a');
	var img = document.createElement('img');
	a.setAttribute('href', url);
	img.setAttribute('alt', name);
	img.setAttribute('src', baseURL + '/assets/images/adopters/' + logo);
	img.setAttribute('class', 'adopters-img');
	a.appendChild(img);
	li.appendChild(a);

	element.appendChild(li);
}