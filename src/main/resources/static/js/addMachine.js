function addElement() {
    let elementContainer = document.getElementById('elements');
    let elementCount = elementContainer.children.length;
    let newElement = document.createElement('tr');
    newElement.innerHTML = `
        <td><input required type="number" min="1" class="form-control" name="elements[${elementCount}].channel"/></td>
        <td><input required type="text" class="form-control" name="elements[${elementCount}].name" /></td>
        <td><input required type="number" min="0" class="form-control" name="elements[${elementCount}].amount" /></td>
        <td><input required type="text" class="form-control" name="elements[${elementCount}].type" /></td>
        <td><button type="button" class="btn btn-danger" onclick="removeElement(this)">Remove</button></td>
    `;
    elementContainer.appendChild(newElement);
}

function removeElement(button) {
    button.parentElement.parentElement.remove();
}

