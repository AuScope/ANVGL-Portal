
function validateOPeNDAPWindow() {
	var win = Ext.getCmp('opendapDownloadFrm');
	var form = win.getForm();

	if (!form.isValid()) {
		Ext.Msg.alert('Invalid Fields','One or more fields are invalid');
		return false;
	}
	
	return true;
}

function getOPeNDAPParameters() {
	//Generates parameters recursively
	//as an array of constraints
	var generateConstraints = function(component) {
		if (!component)
			return null;
		
		if (component.initialConfig.variableType === 'axis') {
			var fromField = component.get(0);
			var toField = component.get(1);
			
			return {
				type		: component.initialConfig.variableType,
				name 		: component.initialConfig.name,
				valueBounds : {
					from		: parseFloat(fromField.value),
					to			: parseFloat(toField.value)
				}
			};
		} else if (component.initialConfig.variableType === 'grid') {
			var childAxes = [];
			for (var i = 0; i < frm.items.getCount(); i++) {
				var child = generateConstraints(component.items.get(i));
				if (child)
					childAxes.push(child);
			}
			return {
				type		: component.initialConfig.variableType,
				name		: component.initialConfig.name,
				axes 		: childAxes
			};
		}
		
		return null;
	};
	
	var frm = Ext.getCmp('opendapDownloadFrm');
	var params = '&opendapUrl=' + escape(Ext.getCmp('opendapUrl').value);
	
	//Generate constraints component
	var variableConstraints = [];
	for (var i = 0; i < frm.items.getCount(); i++) {
		var component = frm.items.get(i);
		
		if (component && !component.disabled) {
			var constraint = generateConstraints(component);
			if (constraint)
				variableConstraints.push(constraint);
		}
	}
	var constraintObj = {
		constraints : variableConstraints
	};
	
	params += '&constraints=' + escape(Ext.util.JSON.encode(constraintObj));
	params += '&downloadFormat=' + escape(Ext.getCmp('opendap-format').value);
	
	return params;
}

/**
 * Shows the OPeNDAP Download window customised for the specified serviceUrl
 * @return
 */
function showOPeNDAPDownload(opendapUrl) {
	
	Ext.QuickTips.init();
	
	var fieldSetsToDisplay = [];
	
	//Completely disables a field set and stops its values from being selected by the "getValues" function
    //This function is recursive over fieldset objects
    var setFieldSetDisabled = function (fieldSet, disabled) {
    	fieldSet.setDisabled(disabled);
    	
    	for (var i = 0; i < fieldSet.items.length; i++) {
    		var item = fieldSet.items.get(i);
    		
    		if (item.getXType() == 'fieldset') {
    			setFieldSetDisabled(item, disabled);
    		} else {
    			item.setDisabled(disabled);
    		}
    	}
    };
	
	var formatsStore = new Ext.data.SimpleStore({
        fields   : ['format'],
        proxy    : new Ext.data.HttpProxy({url: 'opendapGetSupportedFormats.do'}),
        reader : new Ext.data.ArrayReader({}, [
            { name:'format'   }
        ])
    });
	formatsStore.reload();
	
	
	
	fieldSetsToDisplay.push(new Ext.form.FieldSet({
        id				: 'openDapGlobalSpecs',
        title           : 'Required Information',
        items			: [{
            id              : 'opendapUrl',                        
            xtype           : 'textfield',
            fieldLabel      : 'URL',
            value           : opendapUrl,
            name            : 'opendapUrl',
            readOnly		: true,
            anchor          : '-50'                                       
        },{
            xtype			: 'combo',
            id              : 'opendap-format',
            name            : 'format',
            fieldLabel      : 'Format',
            labelAlign      : 'right',
            emptyText       : '',
            forceSelection  : true,
            allowBlank  	: false,
            mode            : 'local',
            store           : formatsStore,
            typeAhead       : true,
            triggerAction   : 'all',
            displayField    : 'format',
            anchor          : '-50',
            valueField      : 'format'        
        }]
    }));
	
	fieldSetsToDisplay.push({
		id				: 'opendap-label-loading',
		xtype			: 'label',
		text			: 'Loading...'
	});
	
	var downloadFile = function(url) {
        var body = Ext.getBody();
        var frame = body.createChild({
            tag:'iframe',
            //cls:'x-hidden',
            id:'iframe',
            name:'iframe'
        });
        var form = body.createChild({
            tag:'form',
            //cls:'x-hidden',
            id:'form',
            target:'iframe',
            method:'POST'
        });
        form.dom.action = url;
        form.dom.submit();
    };
	
	var win = new Ext.Window({
        id              : 'opendapDownloadWindow',        
        autoScroll      : true,
        border          : true,        
        layout          : 'fit',
        resizable       : true,
        modal           : true,
        plain           : false,
        buttonAlign     : 'right',
        title           : 'OPeNDAP Download',
        height          : 600,
        width           : 500,
        items:[{
            // Bounding form
            id      :'opendapDownloadFrm',
            xtype   :'form',
            layout  :'form',
            frame   : true,
            autoHeight : true,
            
            // these are applied to columns
            defaults:{
                xtype: 'fieldset', layout: 'form'
            },
            
            // fieldsets
            items   : fieldSetsToDisplay
        }],
        buttons:[{
                xtype: 'button',
                text: 'Download',
                id: 'opendap-download-button',
                disabled: true,		//will be enabled when variable download completes
                handler: function() {
                    
        			if (!validateOPeNDAPWindow()) {
        				return;
        			}
        	
        			var downloadUrl = './opendapMakeRequest.do?' + getOPeNDAPParameters();
        			downloadFile(downloadUrl);
                }
        }]
    });
    
    win.show();
    
    
    //Recursively generates a field set for a given variable
	var generateVariableFieldSet = function(variable) {
		if (variable.type === 'axis') {
			return {
				xtype		: 'fieldset',
				name		: variable.name,
				variableType: variable.type,
				title		: (variable.name + '[' + variable.valueBounds.from + ', ' + variable.valueBounds.to + ']' + ' - ' + variable.units),
				items		: [{
					xtype		: 'numberfield',
					fieldLabel	: 'From',
					allowBlank	: false,
					value		: variable.valueBounds.from,
					minValue	: variable.valueBounds.from,
					maxValue	: variable.valueBounds.to,
					anchor		: '-50'
				}, {
					xtype		: 'numberfield',
					fieldLabel	: 'To',
					allowBlank	: false,
					value		: variable.valueBounds.to,
					minValue	: variable.valueBounds.from,
					maxValue	: variable.valueBounds.to,
					anchor		: '-50'
				}]
			};
		} else if (variable.type === 'grid') {
			var items = [];
			for (var i = 0; i < variable.axes.length; i++) {
				items.push(generateVariableFieldSet(variable.axes[i]));
			}
			
			return {
				xtype		: 'fieldset',
				title		: variable.name + ' - ' + variable.units,
				name		: variable.name,
				//disabled	: true,
				variableType: variable.type,
				items		: items
			};
		}
		
		throw ('Unable to parse type=' + variable.type);
	};
    
	//Given a list of variables, this function will add the representation of those variable constraints
	//to the specified FormPanel
    var variableListToForm = function (frm, responseObj) {
    	
    	//Our selection model handles adding/removing constraints
    	var cbSm = new Ext.grid.CheckboxSelectionModel({
    		listeners		: {
    			rowdeselect		: function(sm, rowIndex, record) {
    				var fldSet = Ext.getCmp(record.get('componentId'));
    				if (fldSet) {
    					setFieldSetDisabled(fldSet, true);
    					fldSet.setVisible(false);
    				}
    				frm.doLayout();
    			},
    			rowselect		: function(sm, rowIndex, record) {
    				var variableFldSet = Ext.getCmp(record.get('componentId'));
    				if (variableFldSet) {
    					setFieldSetDisabled(variableFldSet, false);
    					variableFldSet.setVisible(true);
    				} else {
	    				var variable = sm.grid.initialConfig.responseObj.variables[rowIndex];
	    				var variableFldSet = generateVariableFieldSet(variable);
	    				
	    				//Configure the ID so it can be referenced later
	    				variableFldSet.id = variable.name + '-fldset';
	    				record.set('componentId', variableFldSet.id);
	    				
	    				frm.add(variableFldSet);
    				}
    				frm.doLayout();
    			}
    		}
    	});
    	
    	//This will parse our response object (the variables inside of it)
    	var variableStore = new Ext.data.JsonStore({
            data   			: responseObj,
            root			: 'variables',
            idProperty		: 'name',
            fields			: ['type', 'name', 'dataType', 'units']
        });
    	
    	//This will house our store in a grid with checkboxes
    	var checkBoxGrid = new Ext.grid.GridPanel({
    		sm 				: cbSm,
    		responseObj		: responseObj,		//store this for reference by selection model
    		store			: variableStore,
    		autoExpandColumn: 'variable-name-col',
    		height			: 100,
    		columns			: [cbSm,{
    			id				: 'variable-name-col',
                header			: "Name",  
                dataIndex		: 'name', 
                sortable		: true, 
                hidden			: false
            }]
    	});
    	
    	//This will wrap our grid with a nice border and title
    	var selectionBoxFieldSet = new Ext.form.FieldSet({
    		title			: 'Available Constraints',
    		id				: 'available-constraints-fldset',
    		items			: [checkBoxGrid],
    		autoHeight		: true
    	});
    	
    	frm.add(selectionBoxFieldSet);
    	frm.doLayout();
    };
    
    //Call this when a variable download fails
    var failVariableDownload = function(errorMessage) {
    	var label = Ext.getCmp('opendap-label-loading');
    	
    	label.setText(errorMessage);
    	
    	Ext.MessageBox.alert('Error', errorMessage);
    };
    
    //Called if our variable download returns a response object
    var successVariableDownload = function(response, options) {
    	var responseObj = Ext.util.JSON.decode(response.responseText);
    	if (!responseObj.success) {
    		failVariableDownload('Error: ' + responseObj.errorMsg);
    		return;
    	}
    	
    	//Update our GUI
    	var label = Ext.getCmp('opendap-label-loading');
    	var button = Ext.getCmp('opendap-download-button');
    	button.setDisabled(false);
    	label.setVisible(false);
    	
    	//Update our form with the downloaded variables
    	var frm = Ext.getCmp('opendapDownloadFrm');
    	variableListToForm(frm, responseObj);
    };
    
    //Download our variable list - this will be used to generate our form parameters
    Ext.Ajax.request({
    	url		: 'opendapGetVariables.do',
    	timeout	: 5 * 60 * 1000, //(5 Minutes)
    	success	: successVariableDownload,
    	failure	: function (response) {
    		failVariableDownload('Error (' + response.status + '): ' + response.statusText);
    	},
    	params	: {
    		opendapUrl : opendapUrl
    	}
    });
}