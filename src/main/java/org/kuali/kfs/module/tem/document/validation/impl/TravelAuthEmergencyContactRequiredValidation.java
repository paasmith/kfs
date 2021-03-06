/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.tem.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelAuthorizationFields;
import org.kuali.kfs.module.tem.businessobject.TravelerDetailEmergencyContact;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelAuthEmergencyContactRequiredValidation extends GenericValidation {
    protected ParameterService parameterService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean rulePassed = true;

        TravelAuthorizationDocument taDocument = (TravelAuthorizationDocument)event.getDocument();
        taDocument.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
        TripType tripType = taDocument.getTripType();

        if (getParameterService().getParameterValueAsBoolean(TravelAuthorizationDocument.class, TravelAuthorizationParameters.DISPLAY_EMERGENCY_CONTACT_IND) && ObjectUtils.isNotNull(tripType)) {
            if (tripType.isContactInfoRequired()  && (taDocument.getDocumentHeader().getWorkflowDocument().isInitiated() || taDocument.getDocumentHeader().getWorkflowDocument().isSaved())) {
                rulePassed = validEmergencyContact(taDocument);
            }

            if (getParameterService().getParameterValuesAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.INTERNATIONAL_TRIP_TYPES).contains(tripType.getCode())) {
                if (StringUtils.isBlank(taDocument.getCellPhoneNumber())) {
                    rulePassed = false;
                    GlobalVariables.getMessageMap().addToErrorPath(KRADPropertyConstants.DOCUMENT);
                    GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.CELL_PHONE_NUMBER, KFSKeyConstants.ERROR_REQUIRED, "Traveler's Cell or Other Contact Number During Trip");
                    GlobalVariables.getMessageMap().removeFromErrorPath(KRADPropertyConstants.DOCUMENT);
                }

                // make sure at least one mode of transportation is filled in
                if(taDocument.getTransportationModes() == null || taDocument.getTransportationModes().size() == 0) {
                    rulePassed = false;
                    GlobalVariables.getMessageMap().addToErrorPath(TemPropertyConstants.EM_CONTACT);
                    GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.MODE_OF_TRANSPORT, TemKeyConstants.ERROR_TA_AUTH_MODE_OF_TRANSPORT_REQUIRED);
                    GlobalVariables.getMessageMap().removeFromErrorPath(TemPropertyConstants.EM_CONTACT);
                }

                // we have an international trip, make sure fields are filled in
                if (StringUtils.isBlank(taDocument.getRegionFamiliarity())) {
                    rulePassed = false;
                    GlobalVariables.getMessageMap().addToErrorPath(KRADPropertyConstants.DOCUMENT);
                    GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.REGION_FAMILIARITY, KFSKeyConstants.ERROR_REQUIRED, "Region Familiarity");
                    GlobalVariables.getMessageMap().removeFromErrorPath(KRADPropertyConstants.DOCUMENT);
                }
            }
        }

        return rulePassed;
    }


    private boolean validEmergencyContact(TravelAuthorizationDocument taDocument){
        // check to see if there are emergency contacts and that at least one of them has real data
        boolean validEmergencyContact = false;
        for (TravelerDetailEmergencyContact emergencyContact : taDocument.getTraveler().getEmergencyContacts()) {
            if (emergencyContact != null && !StringUtils.isBlank(emergencyContact.getContactName())) {
                validEmergencyContact = true;
                return validEmergencyContact;
            }
        }
        if (!validEmergencyContact) {
            //remove the previous error because it could already be in the message map in the wrong order
            GlobalVariables.getMessageMap().removeAllErrorMessagesForProperty(TemPropertyConstants.EM_CONTACT+"."+TemPropertyConstants.TRVL_AUTH_EM_CONTACT_CONTACT_NAME);
            GlobalVariables.getMessageMap().addToErrorPath(TemPropertyConstants.EM_CONTACT);
            GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRVL_AUTH_EM_CONTACT_CONTACT_NAME, TemKeyConstants.ERROR_TA_AUTH_EMERGENCY_CONTACT_REQUIRED);
            GlobalVariables.getMessageMap().removeFromErrorPath(TemPropertyConstants.EM_CONTACT);
        }

        return validEmergencyContact;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
