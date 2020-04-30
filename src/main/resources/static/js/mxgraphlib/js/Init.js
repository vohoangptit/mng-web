// urlParams is null when used for embedding
window.urlParams = window.urlParams || {};

// Public global variables
window.MAX_REQUEST_SIZE = window.MAX_REQUEST_SIZE  || 10485760;
window.MAX_AREA = window.MAX_AREA || 15000 * 15000;

// URLs for save and export
window.EXPORT_URL = window.EXPORT_URL || '/nera/api/export-image-from-workflow';
//window.SAVE_URL = window.SAVE_URL || '/save';
//window.OPEN_URL = window.OPEN_URL || '/open';
//window.RESOURCES_PATH = window.RESOURCES_PATH || 'resources';
window.RESOURCE_BASE = window.RESOURCE_BASE || '/js/mxgraphlib/resources';
window.STENCIL_PATH = window.STENCIL_PATH || 'js/mxgraphlib/stencils';
window.IMAGE_PATH = window.IMAGE_PATH || 'images/mxgraphlib/';
window.STYLE_PATH = window.STYLE_PATH || '/css/mxgraphlib/grapheditor';
window.CSS_PATH = window.CSS_PATH || '/css/mxgraphlib/grapheditor';
window.OPEN_FORM = window.OPEN_FORM || 'open.html';

// Sets the base path, the UI language via URL param and configures the
// supported languages to avoid 404s. The loading of all core language
// resources is disabled as all required resources are in grapheditor.
// properties. Note that in this example the loading of two resource
// files (the special bundle and the default bundle) is disabled to
// save a GET request. This requires that all resources be present in
// each properties file since only one file is loaded.
window.mxBasePath = window.mxBasePath || '/css/mxgraphlib/common/';
window.mxLanguage = window.mxLanguage || '/grapheditor';
//window.mxLanguages = window.mxLanguages || ['de'];
