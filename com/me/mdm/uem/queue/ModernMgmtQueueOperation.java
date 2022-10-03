package com.me.mdm.uem.queue;

import org.json.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.framework.server.queue.DCQueueData;

public class ModernMgmtQueueOperation
{
    int operation;
    ModernMgmtOperationData data;
    private static final String OPERATION_TYPE = "operation_type";
    private static final String DATA = "data";
    
    public ModernMgmtQueueOperation(final int operation, final ModernMgmtOperationData data) {
        this.operation = operation;
        this.data = data;
    }
    
    public void addToModernMgmtOperationQueue() throws Exception {
        final String qFileName = "modernmgmt-task-" + System.currentTimeMillis() + ".txt";
        final DCQueueData queueData = new DCQueueData();
        queueData.fileName = qFileName;
        queueData.postTime = System.currentTimeMillis();
        queueData.queueData = this.serialize();
        final DCQueue queue = DCQueueHandler.getQueue("modernmgmt-task");
        queue.addToQueue(queueData);
    }
    
    private String serialize() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("operation_type", this.operation);
        jsonObject.put("data", (Object)this.data.toJSON());
        return jsonObject.toString();
    }
    
    public static ModernMgmtQueueOperation deSerialize(final String input) {
        final JSONObject jsonObject = new JSONObject(input);
        final int modernOperation = (int)jsonObject.get("operation_type");
        final JSONObject data = jsonObject.getJSONObject("data");
        final ModernMgmtOperationData modernMgmtOperationData = ModernOperation.getDataClass(modernOperation, data);
        final ModernMgmtQueueOperation modernMgmtQueueOperation = new ModernMgmtQueueOperation(modernOperation, modernMgmtOperationData);
        return modernMgmtQueueOperation;
    }
    
    public static class ModernOperation
    {
        public static final int ASSOCIATE_COLLECTION = 1;
        public static final int PROCESS_MODERN_COLLECTION_RESPONSE = 2;
        public static final int UPDATE_LAST_CONTACT = 3;
        
        public static ModernMgmtOperationData getDataClass(final int operation, final JSONObject jsonObject) {
            if (operation == 1) {
                return new ModernMgmtCollectionAssociationData(jsonObject);
            }
            if (operation == 2) {
                return new ModernMgmtCollectionStatusUpdateData(jsonObject);
            }
            if (operation == 3) {
                return new ModernMgmtContactTimeData(jsonObject);
            }
            return null;
        }
    }
}
