/*
 * Copyright 2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.businessobject.lookup;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;

public class ContractsGrantsInvoiceDocumentErrorLogLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject, java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        AnchorHtmlData hRef = (AnchorHtmlData)super.getInquiryUrl(bo, propertyName);
        if (StringUtils.isNotBlank(hRef.getHref())) {
            hRef.setHref(KFSConstants.RICE_PATH_PREFIX + hRef.getHref());
        }
        return hRef;
    }

}
