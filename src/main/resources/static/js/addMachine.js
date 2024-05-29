function addElement() {
    let elementContainer = document.getElementById('elements');
    let elementCount = elementContainer.children.length;
    let newElement = document.createElement('tr');
    let options = types.map(type => `<option value="${type.id}">${type.name} | 1 ${type.measurement} = ${type.unitPrice} UAH</option>`).join('');
    newElement.innerHTML = `
        <td><input required type="text" min="1" class="form-control" name="elements[${elementCount}].channel"/></td>
        <td><input required type="text" class="form-control" name="elements[${elementCount}].name" /></td>
        <td><div class="form-group">
            <select id="type" name="elements[${elementCount}].type.id" class="form-control" required>
                ${options}
            </select>
           </div>
        </td>
        <td><button type="button" class="btn btn-danger" onclick="removeElement(this)">Remove</button></td>
    `;
    elementContainer.appendChild(newElement);
}

function removeElement(button) {
    button.parentElement.parentElement.remove();
}