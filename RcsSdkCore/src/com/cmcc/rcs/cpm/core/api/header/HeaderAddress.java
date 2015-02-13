package com.cmcc.rcs.cpm.core.api.header;

import com.cmcc.rcs.cpm.core.api.address.Address;




public interface HeaderAddress {
    Address getAddress();
    void setAddress(Address address);
}
