// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/** Adds a random fact about myself to the page. */
function addRandomFacts() {
  const facts =
      ['I do not drink boba tea simply because it is boring.', 
       'One of my dream jobs is supermarket cashier.', 
       'I once won a microwave oven in a lucky draw.'];

  // Pick a random fact.
  const fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

/** Fetches comments from the servers and adds them to the DOM. 
    Gets the Blobstore upload URL from the server. */
function getData() {
  fetch('/data?numComment='+document.getElementById("numComment").value).
      then(response => response.json()).then((data) => {
    const arrayListElement = document.getElementById('comments-container');
    arrayListElement.innerHTML = '';
    const numComment = document.getElementById("numComment").value;
    for (i = 0; i < numComment; i++) {
      arrayListElement.appendChild(createListElement(data[i].comment));
    }
    const imgElement = document.getElementById('uploadImg');
    imgElement.src = data[0].imageUrl;
  });

  fetch('/blobstore-upload-url')
      .then((response) => {
        return response.text();
      })
      .then((imageUploadUrl) => {
        const messageForm = document.getElementById('my-form');
        messageForm.action = imageUploadUrl;
      });
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

/** Deletes all comments in DataStore. */
function deleteData(){
  fetch('/delete-data', {method: 'post'});
}