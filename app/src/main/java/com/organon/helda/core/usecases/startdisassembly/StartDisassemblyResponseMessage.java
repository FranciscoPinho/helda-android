package com.organon.helda.core.usecases.startdisassembly;

import com.organon.helda.core.usecases.ResponseMessage;

public class StartDisassemblyResponseMessage extends ResponseMessage<StartDisassemblyResponseMessage> {
    public int disassemblyId;
    public int planId;
}
