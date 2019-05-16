Experiment comparing defaults with custom settings (For Motivation):

**Default Setting:** Ext4 ordered, Delete journal mode, Full synchronization. (WAL size doesn't apply)

**Custom Setting:** F2FS, WAL journal mode, Normal synchronization, WAL size: 1MB

Other parameters: Keep the default setting and change only the corresponding parameter.

**Journal mode:** change from delete mode to WAL. (others no change)
**Synchronization mode:** change from Full to Normal. (others no change)
**File system:** change from Ext4 to F2FS, keeping SQLite parameters unchanged.


**Using the App**

1. Import MyApplication into Android Studio
2. Compile the application to find the apk in the build dir.
3. If not, simply copy the app-debug.apk in the source of this repo to your android device and you should be able to install and use tha app.
(Main source files are at MyApplication/app/src/main/java/)


**Related Papers**
1. Purohith, Dhathri, Jayashree Mohan, and Vijay Chidambaram. "The dangers and complexities of sqlite benchmarking." Proceedings of the 8th Asia-Pacific Workshop on Systems. ACM, 2017.

    [[pdf]](https://www.cs.utexas.edu/~jaya/pdf/apsys17-sqlite.pdf)

2. Mohan, Jayashree, et al. "Storage on your smartphone uses more energy than you think." 9th {USENIX} Workshop on Hot Topics in Storage and File Systems (HotStorage 17). 2017.

    [[pdf]](https://www.cs.utexas.edu/~jaya/pdf/hotstorage17-energy.pdf)
