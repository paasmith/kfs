/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.document.validation.impl;

import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.document.BenefitExpenseTransferDocument;
import org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * Validates that the given accounting line and source lines are the same
 */
public class BenefitExpenseTransferSameAccountValidation extends GenericValidation {
    private BenefitExpenseTransferDocument accountingDocumentForValidation;
    private AccountingLine accountingLineForValidation;
    
    /**
     * Validates that the given accounting lines in the accounting document have 
     * the same account as the source accounting lines. 
     * <strong>Expects an accounting document as the first a parameter</strong>
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        BenefitExpenseTransferDocument benefitExpenseTransferDocument = getAccountingDocumentForValidation() ;
        AccountingLine accountingLine = getAccountingLineForValidation();

        
        if (!hasSameAccount(benefitExpenseTransferDocument, accountingLine)) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, LaborKeyConstants.ERROR_ACCOUNT_NOT_SAME);
            result = false;
        }
        
        return result ;    
    }

    /**
     * Determines whether target accouting lines have the same fringe benefit object codes as source accounting lines
     * 
     * @param accountingDocument the given accounting document
     * @return true if target accouting lines have the same fringe benefit object codes as source accounting lines; otherwise, false
     */
   
    public boolean hasSameAccount(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        boolean accountSame = true ;
        
            LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) accountingDocument;
            List<ExpenseTransferSourceAccountingLine> sourceAccountingLines = expenseTransferDocument.getSourceAccountingLines();

            Account cachedAccount = accountingLine.getAccount();
            for (AccountingLine sourceAccountingLine : sourceAccountingLines) {
                Account account = sourceAccountingLine.getAccount();

                // account number was not retrieved correctly, so the two statements are used to populate the fields manually
                account.setChartOfAccountsCode(sourceAccountingLine.getChartOfAccountsCode());
                account.setAccountNumber(sourceAccountingLine.getAccountNumber());

                if (!account.equals(cachedAccount)) {
                    return false;
                }
            }
       
        return accountSame;
    
     }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public BenefitExpenseTransferDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }    
    
    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingLineForValidation(BenefitExpenseTransferDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
}
