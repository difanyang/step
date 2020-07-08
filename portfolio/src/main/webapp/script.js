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
function loadPage() {
  getData();
  fetchBlobstoreUrl();
  getLoginStatus();
}

function getData() {
  const numComment = document.getElementById('numComment').value;
  fetch('/data?numComment='+numComment)
      .then(response => response.json()).then((data) => {
        const arrayListElement = document.getElementById('inputs-container');
        arrayListElement.innerHTML = '';
        data.forEach((input) => {
          arrayListElement.appendChild(createInputElement(input));
        })
  });
}

function fetchBlobstoreUrl() {
  fetch('/my-blobstore-upload-url')
      .then((response) => {
        return response.text();
      })
      .then((imageUploadUrl) => {
        const inputForm = document.getElementById('input-form');
        inputForm.action = imageUploadUrl;
      });
}

function getLoginStatus() {
  fetch('/login-status')
      .then((response) => {
        return response.text();
      })
      .then((loginStatus) => {
        if (!loginStatus === "Y") {
          const inputForm = document.getElementById('input-form');
          inputForm.style.display = "none";
          const loginElement = document.getElementById('login');
          loginElement.innerHTML = '<p>Login <a href=\"' + loginStatus + '\">here</a>.</p>'
        }
      });
}

/** Creates an element that represents a input. */
function createInputElement(input) {
  const inputElement = document.createElement('li');
  inputElement.className = 'input';

  const emailElement = document.createElement('span');
  emailElement.innerText = input.email + ": ";

  const commentElement = document.createElement('span');
  commentElement.innerText = input.comment;

  const imgElement = document.createElement('img');
  imgElement.src = input.imageUrl;
  
  inputElement.appendChild(emailElement);
  inputElement.appendChild(commentElement);
  inputElement.appendChild(imgElement);
  return inputElement;
}

/** Deletes all comments in DataStore. */
function deleteData(){
  fetch('/delete-data', {method: 'post'});
}