function addRow(name, url, isdir,
	size, size_string, date_modified, date_modified_string) {
	if(name == "." || name == "..")
		return;

	var root = document.location.pathname;
	if(root.substr(-1) !== "/")
		root += "/";

	var tbody = document.getElementById("tbody");
	var row = document.createElement("tr");
	var file_cell = document.createElement("td");
	var link = document.createElement("a");

	link.className = isdir ? "icon dir" : "icon file";

	if(isdir) {
		name = name + "/";
		url = url + "/";
		size = 0;
		size_string = "";
	} else {
		link.draggable = "true";
		link.addEventListener("dragstart", onDragStart, false);
	}
	link.innerText = name;
	link.href = root + url;

	file_cell.dataset.value = name;
	file_cell.appendChild(link);

	row.appendChild(file_cell);
	row.appendChild(createCell(size, size_string));
	row.appendChild(createCell(date_modified, date_modified_string));

	tbody.appendChild(row);
}

function onDragStart(e) {
	var el = e.srcElement;
	var name = el.innerText.replace(":", "");
	var download_url_data = "application/octet-stream:" + name + ":" + el.href;
	e.dataTransfer.setData("DownloadURL", download_url_data);
	e.dataTransfer.effectAllowed = "copy";
}

function createCell(value, text) {
	var cell = document.createElement("td");
	cell.setAttribute("class", "detailsColumn");
	cell.dataset.value = value;
	cell.innerText = text;
	return cell;
}

function start(location) {
	var header = document.getElementById("header");
	header.innerText = header.innerText.replace("LOCATION", location);

	document.getElementById("title").innerText = header.innerText;
}

function onHasParentDirectory() {
	var box = document.getElementById("parentDirLinkBox");
	box.style.display = "block";

	var root = document.location.pathname;
	if(!root.endsWith("/"))
		root += "/";

	var link = document.getElementById("parentDirLink");
	link.href = root + "..";
}

function onListingParsingError() {
	var box = document.getElementById("listingParsingErrorBox");
	box.innerHTML = box.innerHTML.replace("LOCATION", encodeURI(document.location) +
		"?raw");
	box.style.display = "block";
}

function sortTable(column) {
	var theader = document.getElementById("theader");
	var oldOrder = theader.cells[column].dataset.order || '1';
	oldOrder = parseInt(oldOrder, 10)
	var newOrder = 0 - oldOrder;
	theader.cells[column].dataset.order = newOrder;

	var tbody = document.getElementById("tbody");
	var rows = tbody.rows;
	var list = [],
		i;
	for(i = 0; i < rows.length; i++) {
		list.push(rows[i]);
	}

	list.sort(function(row1, row2) {
		var a = row1.cells[column].dataset.value;
		var b = row2.cells[column].dataset.value;
		if(column) {
			a = parseInt(a, 10);
			b = parseInt(b, 10);
			return a > b ? newOrder : a < b ? oldOrder : 0;
		}

		// Column 0 is text.
		if(a > b)
			return newOrder;
		if(a < b)
			return oldOrder;
		return 0;
	});

	// Appending an existing child again just moves it.
	for(i = 0; i < list.length; i++) {
		tbody.appendChild(list[i]);
	}
}

// Add event handlers to column headers.
function addHandlers(element, column) {
	element.onclick = (e) => sortTable(column);
	element.onkeydown = (e) => {
		if(e.key == 'Enter' || e.key == ' ') {
			sortTable(column);
			e.preventDefault();
		}
	};
}

function onLoad() {
	addHandlers(document.getElementById('nameColumnHeader'), 0);
	addHandlers(document.getElementById('sizeColumnHeader'), 1);
	addHandlers(document.getElementById('dateColumnHeader'), 2);
}

window.addEventListener('DOMContentLoaded', onLoad);

let SanitizeInnerHtmlOpts;

var loadTimeData;

function LoadTimeData() {}

(function() {
	'use strict';

	LoadTimeData.prototype = {

		set data(value) {
			expect(!this.data_, 'Re-setting data.');
			this.data_ = value;
		},

		createJsEvalContext: function() {
			return new JsEvalContext(this.data_);
		},

		/**
		 * @param {string} id An ID of a value that might exist.
		 * @return {boolean} True if |id| is a key in the dictionary.
		 */
		valueExists: function(id) {
			return id in this.data_;
		},

		/**
		 * Fetches a value, expecting that it exists.
		 * @param {string} id The key that identifies the desired value.
		 * @return {*} The corresponding value.
		 */
		getValue: function(id) {
			expect(this.data_, 'No data. Did you remember to include strings.js?');
			const value = this.data_[id];
			expect(typeof value != 'undefined', 'Could not find value for ' + id);
			return value;
		},

		/**
		 * As above, but also makes sure that the value is a string.
		 * @param {string} id The key that identifies the desired string.
		 * @return {string} The corresponding string value.
		 */
		getString: function(id) {
			const value = this.getValue(id);
			expectIsType(id, value, 'string');
			return /** @type {string} */ (value);
		},

		getStringF: function(id, var_args) {
			const value = this.getString(id);
			if(!value) {
				return '';
			}

			const args = Array.prototype.slice.call(arguments);
			args[0] = value;
			return this.substituteString.apply(this, args);
		},

		sanitizeInnerHtml: function(rawString, opts) {
			opts = opts || {};
			return parseHtmlSubset('<b>' + rawString + '</b>', opts.tags, opts.attrs)
				.firstChild.innerHTML;
		},

		substituteString: function(label, var_args) {
			const varArgs = arguments;
			return label.replace(/\$(.|$|\n)/g, function(m) {
				assert(m.match(/\$[$1-9]/), 'Unescaped $ found in localized string.');
				return m == '$$' ? '$' : varArgs[m[1]];
			});
		},


		getSubstitutedStringPieces: function(label, var_args) {
			const varArgs = arguments;
			// Split the string by separately matching all occurrences of $1-9 and of
			// non $1-9 pieces.
			const pieces = (label.match(/(\$[1-9])|(([^$]|\$([^1-9]|$))+)/g) || []).map(function(p) {
				// Pieces that are not $1-9 should be returned after replacing $$
				// with $.
				if(!p.match(/^\$[1-9]$/)) {
					assert(
						(p.match(/\$/g) || []).length % 2 == 0,
						'Unescaped $ found in localized string.');
					return {
						value: p.replace(/\$\$/g, '$'),
						arg: null
					};
				}

				// Otherwise, return the substitution value.
				return {
					value: varArgs[p[1]],
					arg: p
				};
			});

			return pieces;
		},

		/**
		 * As above, but also makes sure that the value is a boolean.
		 * @param {string} id The key that identifies the desired boolean.
		 * @return {boolean} The corresponding boolean value.
		 */
		getBoolean: function(id) {
			const value = this.getValue(id);
			expectIsType(id, value, 'boolean');
			return /** @type {boolean} */ (value);
		},

		/**
		 * As above, but also makes sure that the value is an integer.
		 * @param {string} id The key that identifies the desired number.
		 * @return {number} The corresponding number value.
		 */
		getInteger: function(id) {
			const value = this.getValue(id);
			expectIsType(id, value, 'number');
			expect(value == Math.floor(value), 'Number isn\'t integer: ' + value);
			return /** @type {number} */ (value);
		},

		/**
		 * Override values in loadTimeData with the values found in |replacements|.
		 * @param {Object} replacements The dictionary object of keys to replace.
		 */
		overrideValues: function(replacements) {
			expect(
				typeof replacements == 'object',
				'Replacements must be a dictionary object.');
			for(const key in replacements) {
				this.data_[key] = replacements[key];
			}
		}
	};

	/**
	 * Checks condition, displays error message if expectation fails.
	 * @param {*} condition The condition to check for truthiness.
	 * @param {string} message The message to display if the check fails.
	 */
	function expect(condition, message) {
		if(!condition) {
			console.error(
				'Unexpected condition on ' + document.location.href + ': ' + message);
		}
	}

	/**
	 * Checks that the given value has the given type.
	 * @param {string} id The id of the value (only used for error message).
	 * @param {*} value The value to check the type on.
	 * @param {string} type The type we expect |value| to be.
	 */
	function expectIsType(id, value, type) {
		expect(
			typeof value == type, '[' + value + '] (' + id + ') is not a ' + type);
	}

	expect(!loadTimeData, 'should only include this file once');
	loadTimeData = new LoadTimeData;

	// Expose |loadTimeData| directly on |window|. This is only necessary by the
	// auto-generated load_time_data.m.js, since within a JS module the scope is
	// local.
	window.loadTimeData = loadTimeData;
})();