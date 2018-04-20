package com.organon.helda.core.usecases.uploadrecording;

import com.organon.helda.core.usecases.ErrorCode;
import com.organon.helda.core.usecases.RequestHandler;

public class UploadRecording extends RequestHandler<UploadRecordingRequestMessage,UploadRecordingResponseMessage> {
    
        public UploadRecording(com.organon.helda.core.Context context) {
            super(context);
        }

        @Override
        protected boolean isValid(UploadRecordingRequestMessage request) {
            if(!request.payload.exists()) {
                setValidationError("File not found!");
                return false;
            }
            return true;
        }

        @Override
        protected UploadRecordingResponseMessage onValid(UploadRecordingRequestMessage request) {
            if(context.anomalyGateway.sendRecording(request.payload,request.disassembly)==null)
                return new UploadRecordingResponseMessage().error("Upload Failed.", ErrorCode.NOT_FOUND);
            UploadRecordingResponseMessage response = new UploadRecordingResponseMessage();
            return response;
        }

        @Override
        protected UploadRecordingResponseMessage onValidationError(UploadRecordingRequestMessage request) {
            return new UploadRecordingResponseMessage().error(getValidationError(), ErrorCode.VALIDATION_ERROR);
        }
}
