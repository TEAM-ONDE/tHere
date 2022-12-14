import React, { useRef } from 'react';
import styled from 'styled-components';
import ImageDisplayCarousel from './ImageDisplayCarousel';
import PlaceDetailInfo from './PlaceDetailInfo';

const StyledPlaceInfoHolder = styled.div`
  width: 100%;
  height: 100%;
  border: 0.5px solid var(--color-green200);
  display: flex;
  align-items: center;
  padding: 10px;
  border-radius: 10px;
  margin-top: 10px;
`;

// 특정 장소의 정보를 보여주는 컴포넌트
export default function PlaceInfo({ target }) {
  const placeHolder = useRef();
  return (
    <StyledPlaceInfoHolder ref={placeHolder}>
      <ImageDisplayCarousel
        images={target.imageUrls}
        containerRef={placeHolder}
      />
      <PlaceDetailInfo target={target} />
    </StyledPlaceInfoHolder>
  );
}
