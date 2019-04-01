//Exercise 2
//Applying filters to an image
//Jonas Schaefer 1944365

//code apart from the methods applyFilter, createFilter, applySepia and applyGreyscale provided by the lecturer
//any code other than Jonas Schäfer's taken out due to publication restriction

public Color[][] applyFilter(Color[][] pixels, float[][] filter) {

    //create new image of size without edge
    Color[][] newImage = new Color[pixels.length-2][pixels[0].length-2]; 

    //arrays of pixels color values
    double[][] redValue = new double[pixels.length][pixels[0].length];
    double[][] greenValue = new double[pixels.length][pixels[0].length];
    double[][] blueValue = new double[pixels.length][pixels[0].length];

    //pre-declaration
    double newRedValue = 0.0;
    double newGreenValue = 0.0;
    double newBlueValue = 0.0;

    //store Colors in arrays
    for (int row = 0; row < pixels.length; row++) {
        for (int collumn = 0; collumn < pixels[0].length; collumn++) {
            redValue[row][collumn] = pixels[row][collumn].getRed();
            greenValue[row][collumn] = pixels[row][collumn].getGreen();
            blueValue[row][collumn] = pixels[row][collumn].getBlue();
        }
    }

    //for every pixel
    for (int row = 1; row < pixels.length - 1; row++) {
        for (int collumn = 1; collumn < pixels[0].length - 1; collumn++) {

            //for all 3x3 pixels the filter is using
            for (int filterRow = -1; filterRow <= 1; filterRow++) {
                for (int filterCollumn = -1; filterCollumn <= 1; filterCollumn++) {
                    //calculates
                    newRedValue += redValue[row + filterRow][collumn + filterCollumn] * filter[filterRow + 1][filterCollumn + 1];
                    newGreenValue += greenValue[row + filterRow][collumn + filterCollumn] * filter[filterRow + 1][filterCollumn + 1];
                    newBlueValue += blueValue[row + filterRow][collumn + filterCollumn] * filter[filterRow + 1][filterCollumn + 1];
                }
            }

            //round values
            if (newRedValue > 1) {
                newRedValue = 1;
            } else {
                if (newRedValue < 0) {
                    newRedValue = 0;
                }
            }
            if (newGreenValue > 1) {
                newGreenValue = 1;
            } else {
                if (newGreenValue < 0) {
                    newGreenValue = 0;
                }
            }
            if (newBlueValue > 1) {
                newBlueValue = 1;
            } else {
                if (newBlueValue < 0) {
                    newBlueValue = 0;
                }
            }

            //set values
            Color newPixel = new Color(newRedValue, newGreenValue, newBlueValue, 1.0);
            newImage[row - 1][collumn - 1] = newPixel;

            //reset variables
            newRedValue = 0.0;
            newGreenValue = 0.0;
            newBlueValue = 0.0;
        }
    }
    return newImage;
}

// You must complete this method.
public float[][] createFilter(String filterType) {

    if (filterType.equals("IDENTITY")) {
        float[][] identityFilter = {{0,0,0},{0,1,0},{0,0,0}};
        return identityFilter;
    }
    if (filterType.equals("BLUR")) {
        float[][] blurFilter = {{0.0625f,0.125f,0.0625f},{0.125f,0.25f,0.125f},{0.0625f,0.125f,0.0625f}};
        return blurFilter;
    }
    if (filterType.equals("SHARPEN")) {
        float[][] sharpenFilter = {{0,-1,0},{-1,5,-1},{0,-1,0}};
        return sharpenFilter;
    }
    if (filterType.equals("EDGE")) {
        float[][] edgeFilter = {{-1,-1,-1},{-1,8,-1},{-1,-1,-1}};
        return edgeFilter;
    }
    if (filterType.equals("EMBOSS")) {
        float[][] embossFilter = {{-2,-1,0},{-1,0,1},{0,1,2}};
        return embossFilter;
    }
    //otherwise (if wrong input)
    return null;
}

public Color[][] applySepia(Color[][] pixels) {

    //new empty color array 
    Color[][] newImage = new Color[pixels.length][pixels[0].length]; 

    //temporary values within calculation
    double redValue;
    double greenValue;
    double blueValue;

    //temporary values within calculation
    double newRedValue;
    double newGreenValue;
    double newBlueValue;

    //for all pixels
    for (int row = 0; row < pixels.length; row++) {
        for (int collumn = 0; collumn < pixels[0].length; collumn++) {

            //store color values
            redValue = pixels[row][collumn].getRed();
            greenValue = pixels[row][collumn].getGreen();
            blueValue = pixels[row][collumn].getBlue();

            //calculate new values
            newRedValue = redValue * 0.393 + greenValue * 0.769 + blueValue * 0.189;
            newGreenValue = redValue * 0.349 + greenValue * 0.686 + blueValue * 0.168;
            newBlueValue = redValue * 0.272 + greenValue * 0.543 + blueValue * 0.131;

            //round
            if (newRedValue > 1)
                newRedValue = 1;
            if (newGreenValue > 1)
                newGreenValue = 1;
            if (newBlueValue > 1)
                newBlueValue = 1;

            //add new pixel to Color array newImage
            Color newPixel = new Color(newRedValue, newGreenValue, newBlueValue, 1.0);
            newImage[row][collumn] = newPixel;
        }
    }
    //return filtered image
    return newImage;
}

public Color[][] applyGreyscale(Color[][] pixels) {

    //declare new empty color array
    Color[][] newImage = new Color[pixels.length][pixels[0].length];

    //pre-declaration
    double redValue = 0.0;
    double greenValue = 0.0;
    double blueValue = 0.0;
    double greyValue = 0.0;

    //for all pixels
    for (int row = 0; row < pixels.length; row++) {
        for (int collumn = 0; collumn < pixels[0].length; collumn++) {

            //store colors of the pixel
            redValue = pixels[row][collumn].getRed();
            greenValue = pixels[row][collumn].getGreen();
            blueValue = pixels[row][collumn].getBlue();

            //calculate greyvalue and add new grey-filter-pixel
            greyValue = (redValue + greenValue + blueValue)/3;
            Color newPixel = new Color(greyValue, greyValue, greyValue, 1.0);
            newImage[row][collumn] = newPixel;
        }
    }
    //return filtered Color array
    return newImage;
}