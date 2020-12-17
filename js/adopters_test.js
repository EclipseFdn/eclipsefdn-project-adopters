const projectNameMap = {
	"internet-things-iot": ["iot", "technology"],
	"cloud-development-tools": ["ecd"]
}

const eclipsefdn_adopters_element = document.querySelectorAll(".eclipsefdn-adopters")[0];

function testAdopters(working_group) {
	fetch("/adopters/assets/js/adopters.json")
	.then(response => {
		if (response.ok) {
			return response.json();
		} else {
			return Promise.reject(response);
		}
	})
	.then(data => createWGAdopters(data, working_group))
	.catch(error => console.log(error))
}

function createWGAdopters(data, working_group) {
    let project_set = new Set();
    for(let i=0; i<data.length; i++) {

        // Loop the projects of this adopters
        for(let j=0; j<data[i].projects.length; j++) {
            let project = data[i].projects[j];

            // If exists this project
            if( project_set.has(project) ) {

            }

            // If not exists this project
            else {
                project_set.add(project);

                // add the title
                const h2 = document.createElement('h2');
                h2.textContent = project;
                eclipsefdn_adopters_element.append(h2);

                // add the button
                const headerAnchor = document.createElement('a');
                headerAnchor.setAttribute('class', 'btn btn-xs btn-secondary margin-left-10');
                headerAnchor.setAttribute('href', 'https://projects.eclipse.org/projects/' + project);
                headerAnchor.textContent = project.project_id;
                h2.appendChild(headerAnchor);
            }
        }
    }
}