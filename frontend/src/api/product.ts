import { axiosInstanceWithoutBearer, axiosInstanceWithBearer } from './axios';

export const getProducts = async () => {
  const res = await axiosInstanceWithoutBearer.get(`/api/products?`);
  return res;
};

export const postProducts = async (
  formData: FormData,
  // token: string | null,
) => {
  try {
    const res = await axiosInstanceWithBearer.post(
      `/api/products`,
      formData,
      // {
      //   headers: {
      //     Authorization: `Bearer ${token}`,
      //   },
      // }
    );

    if (res.status === 200) {
      console.log('POST 요청이 성공적으로 완료되었습니다.');
    } else {
      console.log('POST 요청이 실패하였습니다.');
    }
  } catch (error) {
    console.log('POST 요청 중 오류가 발생하였습니다.', error);
  }
};

export const getProductsDetail = async (
  productsId: string | undefined,
  token: string | null,
) => {
  if (!productsId) {
    throw new Error('productsId is undefined');
  }

  const res = await axiosInstanceWithBearer.get(`/api/products/${productsId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return res;
};
