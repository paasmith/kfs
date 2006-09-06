/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package org.kuali.module.financial.bo;

import static org.kuali.PropertyConstants.ACCOUNT_NUMBER;
import static org.kuali.PropertyConstants.AMOUNT;
import static org.kuali.PropertyConstants.CHART_OF_ACCOUNTS_CODE;
import static org.kuali.PropertyConstants.CREDIT;
import static org.kuali.PropertyConstants.DEBIT;
import static org.kuali.PropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.PropertyConstants.FINANCIAL_SUB_OBJECT_CODE;
import static org.kuali.PropertyConstants.OBJECT_TYPE_CODE;
import static org.kuali.PropertyConstants.ORGANIZATION_REFERENCE_ID;
import static org.kuali.PropertyConstants.OVERRIDE_CODE;
import static org.kuali.PropertyConstants.PROJECT_CODE;
import static org.kuali.PropertyConstants.REFERENCE_NUMBER;
import static org.kuali.PropertyConstants.REFERENCE_ORIGIN_CODE;
import static org.kuali.PropertyConstants.REFERENCE_TYPE_CODE;
import static org.kuali.PropertyConstants.SUB_ACCOUNT_NUMBER;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.util.SpringServiceLocator;

/**
 * <code>JournalVoucherDocument</code> accounting line parser
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class JournalVoucherAccountingLineParser extends AuxiliaryVoucherAccountingLineParser {
    private String balanceTypeCode;
    private static final String[] NON_OFFSET_ENTRY = { CHART_OF_ACCOUNTS_CODE, ACCOUNT_NUMBER, SUB_ACCOUNT_NUMBER, FINANCIAL_OBJECT_CODE, FINANCIAL_SUB_OBJECT_CODE, PROJECT_CODE, OBJECT_TYPE_CODE, ORGANIZATION_REFERENCE_ID, OVERRIDE_CODE, AMOUNT };
    private static final String[] OFFSET_ENTRY = { CHART_OF_ACCOUNTS_CODE, ACCOUNT_NUMBER, SUB_ACCOUNT_NUMBER, FINANCIAL_OBJECT_CODE, FINANCIAL_SUB_OBJECT_CODE, PROJECT_CODE, OBJECT_TYPE_CODE, ORGANIZATION_REFERENCE_ID, OVERRIDE_CODE, DEBIT, CREDIT };
    private static final String[] ENCUMBRANCE_ENTRY = { CHART_OF_ACCOUNTS_CODE, ACCOUNT_NUMBER, SUB_ACCOUNT_NUMBER, FINANCIAL_OBJECT_CODE, FINANCIAL_SUB_OBJECT_CODE, PROJECT_CODE, OBJECT_TYPE_CODE, ORGANIZATION_REFERENCE_ID, REFERENCE_ORIGIN_CODE, REFERENCE_TYPE_CODE, REFERENCE_NUMBER, OVERRIDE_CODE, DEBIT, CREDIT };

    /**
     * Constructs a JournalVoucherAccountingLineParser.java.
     * 
     * @param balanceTypeCode
     */
    public JournalVoucherAccountingLineParser(String balanceTypeCode) {
        super();
        this.balanceTypeCode = balanceTypeCode;
    }

    /**
     * 
     * @see org.kuali.core.bo.AccountingLineParserBase#performCustomSourceAccountingLinePopulation(java.util.Map,
     *      org.kuali.core.bo.SourceAccountingLine, java.lang.String)
     */
    @Override
    protected void performCustomSourceAccountingLinePopulation(Map<String, String> attributeValueMap, SourceAccountingLine sourceAccountingLine, String accountingLineAsString) {

        boolean isFinancialOffsetGeneration = SpringServiceLocator.getBalanceTypService().getBalanceTypByCode(balanceTypeCode).isFinancialOffsetGenerationIndicator();
        if (isFinancialOffsetGeneration || StringUtils.equals(balanceTypeCode, Constants.BALANCE_TYPE_EXTERNAL_ENCUMBRANCE)) {
            super.performCustomSourceAccountingLinePopulation(attributeValueMap, sourceAccountingLine, accountingLineAsString);
        }
        sourceAccountingLine.setBalanceTypeCode(balanceTypeCode);
    }

    /**
     * @see org.kuali.core.bo.AccountingLineParserBase#getSourceAccountingLineFormat()
     */
    @Override
    public String[] getSourceAccountingLineFormat() {
        return selectFormat();
    }

    /**
     * chooses line format based on balance type code
     * 
     * @return String []
     */
    private String[] selectFormat() {
        if (StringUtils.equals(balanceTypeCode, Constants.BALANCE_TYPE_EXTERNAL_ENCUMBRANCE)) {
            return ENCUMBRANCE_ENTRY;
        }
        else if (SpringServiceLocator.getBalanceTypService().getBalanceTypByCode(balanceTypeCode).isFinancialOffsetGenerationIndicator()) {
            return OFFSET_ENTRY;
        }
        else {
            return NON_OFFSET_ENTRY;
        }
    }
}
