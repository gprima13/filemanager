package com.danamon.filemanager.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class FileInfo {
    private final String name;
    private final boolean directory;
    private final long size;
    private final String modifiedDate;

}
