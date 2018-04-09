// IMyAidlInterface.aidl
package com.yzx.bangbang.Service;

import com.yzx.bangbang.Service.INetworkObserver;
// Declare any non-default types here with import statements

interface INetworkService {
    void connect(int user_id);

    void disconnect();

    void register(INetworkObserver l);

    void unregister(INetworkObserver l);
}
