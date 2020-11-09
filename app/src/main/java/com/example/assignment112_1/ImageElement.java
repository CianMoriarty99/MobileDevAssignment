/*
 * Copyright (c) 2017. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package com.example.assignment112_1;

import java.io.File;

class ImageElement {
    int image=-1;
    File file=null;


    public ImageElement(int image) {
        this.image = image;
    }

    public ImageElement(File fileX) {
        file= fileX;
    }
}
