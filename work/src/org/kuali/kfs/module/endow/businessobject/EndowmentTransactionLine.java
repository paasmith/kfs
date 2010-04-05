/*
 * Copyright 2010 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.businessobject;

import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.util.KualiDecimal;

public interface EndowmentTransactionLine extends PersistableBusinessObject {

    /**
     * @return Returns the kemid.
     */
    public String getKemid();

    /**
     * @param kemid The kemid to set.
     */
    public void setKemid(String kemid);

    /**
     * @return Returns the kemid object.
     */
    public KEMID getKemidObj();

    /**
     * @return Returns the endowment transaction type code.
     */
    public String getEtranCode();

    /**
     * @param endowmentTransactionTypeCode The endowment transaction type code to set.
     */
    public void setEtranCode(String endowmentTransactionTypeCode);

    /**
     * @return Returns the EndowmentTransactionCode object.
     */
    public EndowmentTransactionCode getEtranCodeObj();

    /**
     * @return Returns the transaction line description.
     */
    public String getTransactionLineDescription();

    /**
     * @param description The transaction line description to set.
     */
    public void setTransactionLineDescription(String description);

    /**
     * @return Returns the income/principal indicator code.
     */
    public String getTransactionIncomePrincipalIndicatorCode();

    /**
     * @param ipIndicator The income/principal indicator code to set.
     */
    public void setTransactionIncomePrincipalIndicatorCode(String ipIndicator);

    /**
     * @return Returns the income/principal indicator.
     */
    public IncomePrincipalIndicator getIncomePrincipalIndicator();

    /**
     * @param incomePrincipalIndicator The income/principal indicator to set.
     */
    public void setIncomePrincipalIndicator(IncomePrincipalIndicator incomePrincipalIndicator);

    /**
     * @return Returns the transaction amount.
     */
    public KualiDecimal getTransactionAmount();

    /**
     * @param amount The transaction amount to set.
     */
    public void setTransactionAmount(KualiDecimal amount);

    /**
     * @return Returns the corpus indicator -- Y or N.
     */
    public boolean getCorpusIndicator();

    /**
     * @param corpusIndicator The corpus indicator to set.
     */
    public void setCorpusIndicator(boolean corpusIndicator);

    /**
     * @return Returns the transaction units.
     */
    public KualiDecimal getTransactionUnits();

    /**
     * @param units The transaction units to set.
     */
    public void setTransactionUnits(KualiDecimal units);

    /**
     * @return Returns the line posted indicator -- Y or N.
     */
    public boolean isLinePosted();

    /**
     * @param linePostedIndicator The line posted indicator to set.
     */
    public void setLinePosted(boolean linePostedIndicator);
}
