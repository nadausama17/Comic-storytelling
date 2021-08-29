import cv2
import numpy as np
from PIL import Image
import base64
import io
from scipy.interpolate import UnivariateSpline

def hsv(img, l, u):
    hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
    lower = np.array([l, 128, 128])  # setting lower HSV value
    upper = np.array([u, 255, 255])  # setting upper HSV value
    mask = cv2.inRange(hsv, lower, upper)  # generating mask
    return mask


def spreadLookupTable(x, y):
    spline = UnivariateSpline(x, y)
    return spline(range(256))


def color_quantization(img, k):
    # Transform the image
    data = np.float32(img).reshape((-1, 3))

    # Determine criteria
    criteria = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER, 20, 0.001)

    # Implementing K-Means
    ret, label, center = cv2.kmeans(data, k, None, criteria, 10, cv2.KMEANS_RANDOM_CENTERS)
    center = np.uint8(center)
    result = center[label.flatten()]
    result = result.reshape(img.shape)
    return result


def edge_mask(img, line_size, blur_value):
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    gray_blur = cv2.medianBlur(gray, blur_value)
    edges = cv2.adaptiveThreshold(gray_blur, 255, cv2.ADAPTIVE_THRESH_MEAN_C, cv2.THRESH_BINARY, line_size, blur_value)
    return edges


def cartoonize(img):
    decode_data = base64.b64decode(img)
    np_data = np.fromstring(decode_data, np.uint8)
    image = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)
    line_size = 7
    blur_value = 7
    edges = edge_mask(image, line_size, blur_value)

    total_color = 20
    img = color_quantization(image, total_color)

    blurred = cv2.bilateralFilter(img, d=7, sigmaColor=200, sigmaSpace=200)
    img = cv2.bitwise_and(blurred, blurred, mask=edges)
    pil_im = Image.fromarray(img)
    buff = io.BytesIO()
    pil_im.save(buff, format="PNG")
    img_str = base64.b64encode(buff.getvalue())
    return "" + str(img_str, 'utf-8')
    
def emboss(img):
    decode_data = base64.b64decode(img)
    np_data = np.fromstring(decode_data, np.uint8)
    img = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)
    kernel = np.array([[0, -1, -1],
                       [1, 0, -1],
                       [1, 1, 0]])
    img=cv2.filter2D(img, -1, kernel)
    pil_im = Image.fromarray(img)
    buff = io.BytesIO()
    pil_im.save(buff, format="PNG")
    img_str = base64.b64encode(buff.getvalue())
    return "" + str(img_str, 'utf-8')
    
def gaussianBlur(img):
    decode_data = base64.b64decode(img)
    np_data = np.fromstring(decode_data, np.uint8)
    img = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)
    img=cv2.GaussianBlur(img, (35, 35), 0)
    pil_im = Image.fromarray(img)
    buff = io.BytesIO()
    pil_im.save(buff, format="PNG")
    img_str = base64.b64encode(buff.getvalue())
    return "" + str(img_str, 'utf-8')
    
def sharpen(img):
    decode_data = base64.b64decode(img)
    np_data = np.fromstring(decode_data, np.uint8)
    img = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)
    kernel = np.array([[-1, -1, -1], [-1, 9, -1], [-1, -1, -1]])
    img=cv2.filter2D(img, -1, kernel)
    pil_im = Image.fromarray(img)
    buff = io.BytesIO()
    pil_im.save(buff, format="PNG")
    img_str = base64.b64encode(buff.getvalue())
    return "" + str(img_str, 'utf-8')
    
def sepia(img):
    decode_data = base64.b64decode(img)
    np_data = np.fromstring(decode_data, np.uint8)
    img = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)
    img = np.array(img, dtype=np.float64)  # converting to float to prevent loss
    img = cv2.transform(img, np.matrix([[0.272, 0.534, 0.131],
                                        [0.349, 0.686, 0.168],
                                        [0.393, 0.769, 0.189]]))  # multipying image with special sepia matrix
    img[np.where(img > 255)] = 255  # normalizing values greater than 255 to 255
    img = np.array(img, dtype=np.uint8)  # converting back to int
    pil_im = Image.fromarray(img)
    buff = io.BytesIO()
    pil_im.save(buff, format="PNG")
    img_str = base64.b64encode(buff.getvalue())
    return "" + str(img_str, 'utf-8')

def splash(img):
    decode_data = base64.b64decode(img)
    np_data = np.fromstring(decode_data, np.uint8)
    img = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)
    res = np.zeros(img.shape, np.uint8)  # creating blank mask for result
    l = 15  # the lower range of Hue we want
    u = 30  # the upper range of Hue we want
    mask = hsv(img, l, u)
    inv_mask = cv2.bitwise_not(mask)  # inverting mask
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    res1 = cv2.bitwise_and(img, img, mask=mask)  # region which has to be in color
    res2 = cv2.bitwise_and(gray, gray, mask=inv_mask)  # region which has to be in grayscale
    for i in range(3):
        res[:, :, i] = res2  # storing grayscale mask to all three slices
    img = cv2.bitwise_or(res1, res)  # joining grayscale and color region
    pil_im = Image.fromarray(img)
    buff = io.BytesIO()
    pil_im.save(buff, format="PNG")
    img_str = base64.b64encode(buff.getvalue())
    return "" + str(img_str, 'utf-8')
    
def coldImage(img):
    decode_data = base64.b64decode(img)
    np_data = np.fromstring(decode_data, np.uint8)
    img = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)
    increaseLookupTable = spreadLookupTable([0, 64, 128, 256], [0, 80, 160, 256])
    decreaseLookupTable = spreadLookupTable([0, 64, 128, 256], [0, 50, 100, 256])
    red_channel, green_channel, blue_channel = cv2.split(img)
    red_channel = cv2.LUT(red_channel, decreaseLookupTable).astype(np.uint8)
    blue_channel = cv2.LUT(blue_channel, increaseLookupTable).astype(np.uint8)
    img= cv2.merge((red_channel, green_channel, blue_channel))
    pil_im = Image.fromarray(img)
    buff = io.BytesIO()
    pil_im.save(buff, format="PNG")
    img_str = base64.b64encode(buff.getvalue())
    return "" + str(img_str, 'utf-8')
    
def warmImage(img):
    decode_data = base64.b64decode(img)
    np_data = np.fromstring(decode_data, np.uint8)
    img = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)
    increaseLookupTable = spreadLookupTable([0, 64, 128, 256], [0, 80, 160, 256])
    decreaseLookupTable = spreadLookupTable([0, 64, 128, 256], [0, 50, 100, 256])
    red_channel, green_channel, blue_channel = cv2.split(img)
    red_channel = cv2.LUT(red_channel, increaseLookupTable).astype(np.uint8)
    blue_channel = cv2.LUT(blue_channel, decreaseLookupTable).astype(np.uint8)
    img= cv2.merge((red_channel, green_channel, blue_channel))
    pil_im = Image.fromarray(img)
    buff = io.BytesIO()
    pil_im.save(buff, format="PNG")
    img_str = base64.b64encode(buff.getvalue())
    return "" + str(img_str, 'utf-8')
    
def pencil_blackandwhite(img):
    decode_data = base64.b64decode(img)
    np_data = np.fromstring(decode_data, np.uint8)
    img = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)
    dst_gray, dst_color = cv2.pencilSketch(img, sigma_s=60, sigma_r=0.07,
                                           shade_factor=0.05)  # inbuilt function to generate pencil sketch in both color and grayscale
    # sigma_s controls the size of the neighborhood. Range 1 - 200
    # sigma_r controls the how dissimilar colors within the neighborhood will be averaged. A larger sigma_r results in large regions of constant color. Range 0 - 1
    # shade_factor is a simple scaling of the output image intensity. The higher the value, the brighter is the result. Range 0 - 0.1
    img=dst_gray
    pil_im = Image.fromarray(img)
    buff = io.BytesIO()
    pil_im.save(buff, format="PNG")
    img_str = base64.b64encode(buff.getvalue())
    return "" + str(img_str, 'utf-8')

def pencil_color(img):
    decode_data = base64.b64decode(img)
    np_data = np.fromstring(decode_data, np.uint8)
    img = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)
    dst_gray, dst_color = cv2.pencilSketch(img, sigma_s=60, sigma_r=0.07,
                                           shade_factor=0.05)  # inbuilt function to generate pencil sketch in both color and grayscale
    # sigma_s controls the size of the neighborhood. Range 1 - 200
    # sigma_r controls the how dissimilar colors within the neighborhood will be averaged. A larger sigma_r results in large regions of constant color. Range 0 - 1
    # shade_factor is a simple scaling of the output image intensity. The higher the value, the brighter is the result. Range 0 - 0.1
    img= dst_color
    pil_im = Image.fromarray(img)
    buff = io.BytesIO()
    pil_im.save(buff, format="PNG")
    img_str = base64.b64encode(buff.getvalue())
    return "" + str(img_str, 'utf-8')