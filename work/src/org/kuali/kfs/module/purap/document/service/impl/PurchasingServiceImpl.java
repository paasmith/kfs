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
package org.kuali.kfs.module.purap.document.service.impl;

import java.util.List;

import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.service.PurchasingDocumentSpecificService;
import org.kuali.kfs.module.purap.document.service.PurchasingService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.service.impl.PersistenceServiceStructureImplBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PurchasingServiceImpl extends PersistenceServiceStructureImplBase implements PurchasingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingServiceImpl.class);

    private ParameterService parameterService;
    private SequenceAccessorService sequenceAccessorService;
    private CapitalAssetBuilderModuleService capitalAssetBuilderModuleService;
    
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setSequenceAccessorService(SequenceAccessorService sequenceAccessorService) {
        this.sequenceAccessorService = sequenceAccessorService;
    }
    
    public void setCapitalAssetBuilderModuleService(CapitalAssetBuilderModuleService capitalAssetBuilderModuleService) {
        this.capitalAssetBuilderModuleService = capitalAssetBuilderModuleService;
    }

    public void saveDocumentWithoutValidation(PurchasingDocument document) {
        document.getDocumentSpecificService().saveDocumentWithoutValidation(document);
    }

    public void setupCapitalAssetItems(PurchasingDocument purDoc) {

        List<PurchasingCapitalAssetItem> camsItemsList = purDoc.getPurchasingCapitalAssetItems();
        List<PurchasingCapitalAssetItem> newCamsItemsList = new TypedArrayList(purDoc.getPurchasingCapitalAssetItemClass());
        
        for (PurApItem purapItem : purDoc.getItems()) {
            if (purapItem.getItemType().isItemTypeAboveTheLineIndicator()) {
                if (capitalAssetBuilderModuleService.doesItemNeedCapitalAsset(purapItem)) {
                    PurchasingCapitalAssetItem camsItem = getItemIfAlreadyInCamsItemsList(purapItem, camsItemsList);
                    //If either the camsItem is null or if its system is null and the document's system type is IND (this is
                    //the case when the user tries to switch from ONE system type to IND system type), we'll have to create
                    //the camsItem again along with its system to prevent the No collection found error.
                    if (ObjectUtils.isNull(camsItem) || (purDoc.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL) && ObjectUtils.isNull(camsItem.getPurchasingCapitalAssetSystem())) ) {
                        PurchasingCapitalAssetItem newCamsItem = createCamsItem(purDoc, purapItem);
                        newCamsItemsList.add(newCamsItem);
                    }
                    else {
                        newCamsItemsList.add(camsItem);
                    }
                }
                else {
                    PurchasingCapitalAssetItem camsItem = getItemIfAlreadyInCamsItemsList(purapItem, camsItemsList);
                    if (camsItem != null && camsItem.isEmpty()) {
                        camsItemsList.remove(camsItem);
                    }
                }
            }
        }

        purDoc.setPurchasingCapitalAssetItems(newCamsItemsList);
    }
    
    private PurchasingCapitalAssetItem createCamsItem(PurchasingDocument purDoc, PurApItem purapItem) {
        PurchasingDocumentSpecificService purchasingDocumentSpecificService = purDoc.getDocumentSpecificService();
        if (purapItem.getItemIdentifier() == null) {
            ClassDescriptor cd = this.getClassDescriptor(purapItem.getClass());
            String sequenceName = cd.getFieldDescriptorByName(PurapPropertyConstants.ITEM_IDENTIFIER).getSequenceName();
            Integer itemIdentifier = new Integer(sequenceAccessorService.getNextAvailableSequenceNumber(sequenceName).toString());
            purapItem.setItemIdentifier(itemIdentifier);
        }
        PurchasingCapitalAssetItem camsItem = purchasingDocumentSpecificService.createCamsItem(purDoc, purapItem);
        return camsItem;
    }


    private PurchasingCapitalAssetItem getItemIfAlreadyInCamsItemsList(PurApItem item, List<PurchasingCapitalAssetItem> camsItemsList) {
        for (PurchasingCapitalAssetItem camsItem : camsItemsList) {
            if (camsItem.getItemIdentifier() != null && camsItem.getItemIdentifier().equals(item.getItemIdentifier())) {
                return camsItem;
            }
        }

        return null;
    }


    public void deleteCapitalAssetItems(PurchasingDocument purDoc, Integer itemIdentifier) {
        // delete the corresponding CAMS items.
        int index = 0;
        for (PurchasingCapitalAssetItem camsItem : purDoc.getPurchasingCapitalAssetItems()) {
            if (camsItem.getItemIdentifier().equals(itemIdentifier)) {
                break;
            }
            index++;
        }
        purDoc.getPurchasingCapitalAssetItems().remove(index);
    }

    public void setupCapitalAssetSystem(PurchasingDocument purDoc) {
        CapitalAssetSystem resultSystem = purDoc.getDocumentSpecificService().createCapitalAssetSystem();
        if (purDoc.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM) || purDoc.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS)) {
            if (purDoc.getPurchasingCapitalAssetSystems().size() == 0) {
                purDoc.getPurchasingCapitalAssetSystems().add(resultSystem);
            }
        }
    }
    
    public void prorateDiscountTradeIn(PurchasingDocument purDoc) {
        //purapAccountingLineService.generateAccountDistributionForProration
        
    }

    
    public String getDefaultAssetTypeCodeNotThisFiscalYear() {
        return parameterService.getParameterValue(ParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, PurapParameterConstants.CapitalAsset.PURCHASING_DEFAULT_ASSET_TYPE_WHEN_NOT_THIS_FISCAL_YEAR);
    }
}