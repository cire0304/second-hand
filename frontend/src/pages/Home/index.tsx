import { useNavigate } from 'react-router-dom';

import * as S from './styles';

import NavBarHome from '../../components/NavBarHome';
import SecondHandItem from '../../components/SecondHandItem';
import ErrorPage from '../Error';
import { CATEGORY, ITEM_DETAIL, SALES_ITEM } from '../../constants/routeUrl';
import Button from '../../components/Button';
import Icon from '../../components/Icon';
import useAsync from '../../hooks/useAsync';
import { getProducts } from '../../api/product';
import { ACCESS_TOKEN } from '../../constants/login';
import { getMembers } from '../../api/member';

interface Item {
  productId: number;
  createAt: string;
  title: string;
  salesStatus: '판매중' | '예약중' | '판매완료';
  updatedAt: string;
  price: number | null;
  location: string;
  chatRoomCount: number;
  watchlistCount: number;
  isWatchlistChecked: boolean;
  productMainImgUrl: string;
  isSetEditOption?: boolean;
}

// TODO : 무한 스크롤 구현하기

const defaultLocation = [
  {
    locationDetails: '서울특별시 강남구 역삼1동',
    locationShortening: '역삼1동',
    // TODO : 로케이션 ID 추가하기
  },
];

const HomePage = () => {
  const navigate = useNavigate();
  const accessToken = localStorage.getItem(ACCESS_TOKEN);

  const { data } = useAsync(() => getProducts());
  const itemList = data?.data;
  const isResultEmpty: boolean = itemList?.length === 0;

  const { data: userData } = useAsync(() => getMembers(accessToken));
  const userLocationDatas = userData?.data?.locationDatas || defaultLocation;

  const handleIconClick = () => {
    navigate(CATEGORY);
  };

  const handleFABClick = () => {
    navigate(SALES_ITEM);
  };

  const handleItemClick = (productId: number) => {
    navigate(`${ITEM_DETAIL}/${productId}`);
  };

  return (
    <>
      <NavBarHome
        type="medium"
        iconOnClick={handleIconClick}
        userLocationDatas={userLocationDatas}
      />
      {!isResultEmpty ? (
        <div>
          {itemList?.map((item: Item) => {
            return (
              <li
                key={item.productId}
                onClick={() => handleItemClick(item.productId)}
              >
                <SecondHandItem
                  title={item.title}
                  updatedAt={item.updatedAt}
                  salesStatus={item.salesStatus}
                  price={item.price}
                  location={item.location}
                  chatRoomCount={item.chatRoomCount}
                  watchlistCount={item.watchlistCount}
                  isWatchlistChecked={item.isWatchlistChecked}
                  productMainImgUrl={item.productMainImgUrl}
                  isSetEditOption={false}
                />
              </li>
            );
          })}
        </div>
      ) : (
        <ErrorPage />
      )}
      <S.ButtonPosition>
        <Button circle active onClick={handleFABClick}>
          <Icon name="symbol" width="18" height="20" />
        </Button>
      </S.ButtonPosition>
    </>
  );
};

export default HomePage;
