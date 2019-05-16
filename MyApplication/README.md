Experiment comparing defaults with custom settings (For Motivation):

**Default Setting:** Ext4 ordered, Delete journal mode, Full synchronization. (WAL size doesn't apply)

**Custom Setting:** F2FS, WAL journal mode, Normal synchronization, WAL size: 1MB

Other parameters: Keep the default setting and change only the corresponding parameter.

**Journal mode:** change from delete mode to WAL. (others no change)
**Synchronization mode:** change from Full to Normal. (others no change)
**File system:** change from Ext4 to F2FS, keeping SQLite parameters unchanged.